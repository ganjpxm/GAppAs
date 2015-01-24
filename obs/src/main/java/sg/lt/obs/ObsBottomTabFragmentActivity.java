package sg.lt.obs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;

import java.io.IOException;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.gcm.GcmUtil;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.fragment.FragmentIndicator;
import sg.lt.obs.fragment.FragmentIndicator.OnIndicateListener;
import sg.lt.obs.service.LocationUpdateService;

public class ObsBottomTabFragmentActivity extends FragmentActivity { //implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener

	public static Fragment[] mFragments;
	static final String TAG = "ObsBottomTabFragmentActivity";
	
	//GCM
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging mGoogleCloudMessaging;
    private Context mContext;
    private Activity mActivity;
    private String mRegistrationId;

    //Location
//    protected GoogleApiClient mGoogleApiClient;
//    protected LocationRequest mLocationRequest;
//    protected Boolean mRequestingLocationUpdates = true;
//    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;
//    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_fragment);

		setFragmentIndicator(0);
		
		//  Check device for Play Services APK. If check succeeds, proceed with GCM registration.
		mContext = getApplicationContext();
		mActivity = this;
        if (checkPlayServices()) {
            mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(this);
            mRegistrationId = GcmUtil.getRegistrationId(mContext);

            if (StringUtil.isEmpty(mRegistrationId)) {
                registerInBackground();
            }
        } else {
        	mHandler.obtainMessage(0).sendToTarget();
        }

        startService(new Intent(this, LocationUpdateService.class));
//        buildGoogleApiClient();
//        if (mGoogleApiClient!=null) {
//            mGoogleApiClient.connect();
//        }
	}
	
	private Handler mHandler = new Handler() {  
    	public void handleMessage (Message msg) {
    		if (msg.what==0) {
    			DialogUtil.showAlertDialog(mActivity, "No valid Google Play Services APK found.");
    		}
    		
    	}  
    }; 
	
	private void setFragmentIndicator(int whichIsDefault) {
		mFragments = new Fragment[4];
		mFragments[0] = getSupportFragmentManager().findFragmentById(R.id.fragment_booking_add);
		mFragments[1] = getSupportFragmentManager().findFragmentById(R.id.fragment_booking_history);
		mFragments[2] = getSupportFragmentManager().findFragmentById(R.id.fragment_my_profile);
		mFragments[3] = getSupportFragmentManager().findFragmentById(R.id.fragment_more);
		getSupportFragmentManager().beginTransaction().hide(mFragments[0]).hide(mFragments[1])
            .hide(mFragments[2]).hide(mFragments[3]).show(mFragments[whichIsDefault]).commit();

		FragmentIndicator mIndicator = (FragmentIndicator) findViewById(R.id.indicator);
		FragmentIndicator.setIndicator(whichIsDefault);
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View view, int which) {
			    getSupportFragmentManager().beginTransaction().hide(mFragments[0]).hide(mFragments[1])
                    .hide(mFragments[2]).hide(mFragments[3]).show(mFragments[which]).commit();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
        checkPlayServices();
		ActivityStack.setActiveActivity(this);
        //final String mBookingVehicleItemId = extras.getString("bookingVehicleItemId");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	}
	
	@Override
	protected void onPause() {
		ActivityStack.setActiveActivity(null);
		super.onPause();
	}

    @Override
    protected void onStop() {
        super.onStop();
//        if (mGoogleApiClient!=null) {
//            mGoogleApiClient.connect();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mGoogleApiClient!=null) {
//            mGoogleApiClient.connect();
//        }
    }
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	

    /**
     * <p>Registers the application with GCM servers asynchronously.
     * Stores the registration ID and the app versionCode in the application's shared preferences.</p>
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGoogleCloudMessaging == null) {
                        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(mContext);
                    }
                    mRegistrationId = mGoogleCloudMessaging.register(ObsConst.APP_PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + mRegistrationId;

                    // You should send the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send  upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
//                    storeRegistrationId(context, mRegistrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                
            }
        }.execute(null, null, null);
    }
    
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
    	try {
    		GcmUtil.storeRegistrationId(mContext, mRegistrationId);
	    	String result = ObsUtil.registDevice(mRegistrationId, Const.VALUE_YES);
	    	if (Const.VALUE_SUCCESS.equalsIgnoreCase(result)) {
	    		PreferenceUtil.saveString(mRegistrationId, Const.VALUE_YES);
	    	} else {
	    		PreferenceUtil.saveString(mRegistrationId, Const.VALUE_NO);
	    	}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

//    /**
//     * Runs when a GoogleApiClient object successfully connects.
//     */
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        Log.i(TAG, "Connected to GoogleApiClient");
//        if (ObsUtil.sLastLocation == null) {
//            ObsUtil.sLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        }
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
//    }
//
//    public void updateLastLocationInfo() {
//        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(ObsUtil.sLastLocation.getLatitude(), ObsUtil.sLastLocation.getLongitude(), 1);
//            Address address = addresses.get(0);
//            String addressLine = address.getAddressLine(0);
//            if (StringUtil.hasText(addressLine)) {
//                if (addressLine.indexOf(address.getCountryName())==-1) {
//                    addressLine += ", " + address.getCountryName();
//                }
//
//                if (StringUtil.isEmpty(ObsUtil.sLastAddress) || !addressLine.equals(ObsUtil.sLastAddress)) {
//                    ObsUtil.sLastAddress = addressLine;
//                    ObsUtil.sLastDateTime = DateUtil.formateDate(new Date(), "dd/MM/yyyy HH:mm:ss");
//                    new Thread(new Runnable() {
//                        public void run() {
//                            ObsUtil.trackLocation();
//                        }
//                    }).start();
//                } else if (StringUtil.hasText(ObsUtil.sLastAddress) && addressLine.equals(ObsUtil.sLastAddress)) {
//                    String nowHours = DateUtil.formateDate(new Date(), "HH");
//                    String lastHours = DateUtil.formateDate(DateUtil.parseDateOrDateTime(ObsUtil.sLastDateTime), "HH");
//                    if (!nowHours.equals(lastHours)) {
//                        ObsUtil.sLastDateTime = DateUtil.formateDate(new Date(), "dd/MM/yyyy HH:mm:ss");
//                        new Thread(new Runnable() {
//                            public void run() {
//                                ObsUtil.trackLocation();
//                            }
//                        }).start();
//                    }
//                }
//            }
//
//
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * Callback that fires when the location changes.
//     */
//    @Override
//    public void onLocationChanged(Location location) {
//        ObsUtil.sLastLocation = location;
//        updateLastLocationInfo();
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        // The connection to Google Play services was lost for some reason. We call connect() to attempt to re-establish the connection.
//        Log.i(TAG, "Connection suspended");
//        mGoogleApiClient.connect();
//    }
//
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
//        // onConnectionFailed.
//        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
//    }
//
//    /**
//     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
//     * LocationServices API.
//     */
//    protected synchronized void buildGoogleApiClient() {
//        Log.i(TAG, "Building GoogleApiClient");
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        createLocationRequest();
//    }
//
//    /**
//     * Sets up the location request. Android has two location request settings:
//     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
//     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
//     * the AndroidManifest.xml.
//     * <p/>
//     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
//     * interval (5 seconds), the Fused Location Provider API returns location updates that are
//     * accurate to within a few feet.
//     * <p/>
//     * These settings are appropriate for mapping applications that show real-time location
//     * updates.
//     */
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//
//        // Sets the desired interval for active location updates. This interval is
//        // inexact. You may not receive updates at all if no location sources are available, or
//        // you may receive them slower than requested. You may also receive updates faster than
//        // requested if other applications are requesting location at a faster interval.
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        // Sets the fastest rate for active location updates. This interval is exact, and your application will never receive updates faster than this value.
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//    }
//
//    /**
//     * Requests location updates from the FusedLocationApi.
//     */
//    protected void startLocationUpdates() {
//        // The final argument to {@code requestLocationUpdates()} is a LocationListener
//        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
}
