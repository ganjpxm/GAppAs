/**
 * SplashActivity.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs;

import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.ThreadUtil;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;

import android.content.Intent;
import android.os.Bundle;

import java.util.Map;

/**
 * <p>Splash screen activity</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class SplashActivity extends ObsActivity {
    boolean isTimeout = false;
    private static Thread mTimeoutThread;
    String userId = "";
    private Map<String,String> mResultMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_activity_splash);
		userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
		if (NetworkUtil.isNetworkAvailable(this) && StringUtil.isNotEmpty(userId)) {
            showProgressBar();
			mTimeoutThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
                isTimeout = true;
                try {
                    Thread.sleep(Const.TIMEOUT_CONNECT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isTimeout) {
                    forward(false);
                    showToastFromBackground(Const.VALUE_TIMEOUT);
                }
	            }
	        });
			mTimeoutThread.start();
			
			new Thread(new Runnable() {
				public void run() {
                try {
                    mResultMap = ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                    isTimeout = false;
                    if (mTimeoutThread!=null) {
                        mTimeoutThread.interrupt();
                        mTimeoutThread=null;
                    }
                    Thread.sleep(Const.DURATION_SPLASH);
                    forward(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    forward(false);
                    showToastFromBackground(Const.VALUE_FAIL);
                }
				}
			}).start();
		} else {
			ThreadUtil.run(new Runnable() {
	            @Override
	            public void run() {
	                forward(false);
	            }
	        }, Const.DURATION_SPLASH);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void forward(boolean isShowBroadcast) {
		Intent intent = null;
	    if (StringUtil.isNotEmpty(userId)) {
            if (isShowBroadcast) {
                if (mResultMap !=null && StringUtil.hasText(mResultMap.get(ObsConst.KEY_BROADCAST_BOOKING_VEHICLE_ITEM_IDS))) {
                    intent = new Intent(SplashActivity.this, BookingVehicleAlarmListActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, ObsBottomTabFragmentActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, ObsBottomTabFragmentActivity.class);
            }
		} else {
			intent = new Intent(SplashActivity.this, DriverLoginActivity.class);
		}
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
	}
}
	
	