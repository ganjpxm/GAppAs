package sg.lt.obs;

import java.io.IOException;

import org.ganjp.glib.core.ActivityStack;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.Const;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.gcm.GcmUtil;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.fragment.FragmentIndicator;
import sg.lt.obs.fragment.FragmentIndicator.OnIndicateListener;

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

public class ObsBottomTabFragmentActivity extends FragmentActivity {

	public static Fragment[] mFragments;
	static final String TAG = "ObsBottomTabFragmentActivity";
	
	//GCM
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	GoogleCloudMessaging mGoogleCloudMessaging;
    Context mContext;
    Activity mActivity;
    String mRegistrationId;
    String acceptResult;

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
	}
	
	private Handler mHandler = new Handler() {  
    	public void handleMessage (Message msg) {
    		if (msg.what==0) {
    			DialogUtil.showAlertDialog(mActivity, "No valid Google Play Services APK found.");
    		} else if (msg.what==1) {
    			DialogUtil.showAlertDialog(mActivity, acceptResult);
    		} else if (msg.what==2) {
    			DialogUtil.showAlertDialog(mActivity, "Accept Fail");
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
    

	
}
