package sg.lt.obs.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.lt.obs.common.other.ObsUtil;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    static final String TAG = "BackgroundLocationService";
    IBinder mBinder = new LocalBinder();

    //Location
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private boolean mInProgress;
    protected Boolean mServicesAvailable = true;
    protected PowerManager.WakeLock mWakeLock;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public LocationUpdateService() {
    }

    public class LocalBinder extends Binder {
        public LocationUpdateService getServerInstance() {
            return LocationUpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInProgress = false;
        mServicesAvailable = servicesConnected();
        buildGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        super.onStartCommand(intent, flags, startId);

        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE); //*** added this
        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock"); //*** added this
        mWakeLock.acquire(); //*** added this

        if(!mServicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;

        setUpGoogleApiClientIfNeeded();
        if(!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress)
        {
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    private void setUpGoogleApiClientIfNeeded()
    {
        if(mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }

        // Turn off the request flag
        mInProgress = false;
        if(mServicesAvailable && mGoogleApiClient != null) {
            stopLocationUpdates();
            // Destroy the current location client
            mGoogleApiClient = null;
        }
        mWakeLock.release(); ///**** added this
        super.onDestroy();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {

            return false;
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (ObsUtil.sLastLocation == null) {
            ObsUtil.sLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        startLocationUpdates();
    }

    public void updateLastLocationInfo() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(ObsUtil.sLastLocation.getLatitude(), ObsUtil.sLastLocation.getLongitude(), 1);
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);
            if (StringUtil.hasText(addressLine)) {
                if (addressLine.indexOf(address.getCountryName())==-1) {
                    addressLine += ", " + address.getCountryName();
                }

                if (StringUtil.isEmpty(ObsUtil.sLastAddress) || !addressLine.equals(ObsUtil.sLastAddress)) {
                    ObsUtil.sLastAddress = addressLine;
                    ObsUtil.sLastDateTime = DateUtil.formateDate(new Date(), "dd/MM/yyyy HH:mm:ss");
                    new Thread(new Runnable() {
                        public void run() {
                            ObsUtil.trackLocation();
                        }
                    }).start();
                } else if (StringUtil.hasText(ObsUtil.sLastAddress) && addressLine.equals(ObsUtil.sLastAddress)) {
                    String nowHours = DateUtil.formateDate(new Date(), "HH");
                    String lastHours = DateUtil.formateDate(DateUtil.parseDateOrDateTime(ObsUtil.sLastDateTime), "HH");
                    if (!nowHours.equals(lastHours)) {
                        ObsUtil.sLastDateTime = DateUtil.formateDate(new Date(), "dd/MM/yyyy HH:mm:ss");
                        new Thread(new Runnable() {
                            public void run() {
                                ObsUtil.trackLocation();
                            }
                        }).start();
                    }
                }
            }


        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        ObsUtil.sLastLocation = location;
        updateLastLocationInfo();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
}
