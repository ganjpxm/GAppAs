/**
 * SplashActivity.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package com.onetransport.obs;

import org.ganjp.glib.core.Const;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.ThreadUtil;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.activity.ObsActivity;
import com.onetransport.obs.common.other.ObsUtil;
import com.onetransport.obs.common.other.PreferenceUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * <p>Splash screen activity</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class SplashActivity extends ObsActivity {
	ProgressBar progressBar;
    boolean isTimeout = false;
    private static Thread mTimeoutThread;
    String jsonData = "";
    String userId = "";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_activity_splash);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBSD);
		if (NetworkUtil.isNetworkAvailable(this) && StringUtil.isNotEmpty(userId)) {
			progressBar.setVisibility(TextView.VISIBLE);
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
	                	forward();
	                	showToastFromBackground(ObsConst.VALUE_TIMEOUT);
	                }
	            }
	        });
			mTimeoutThread.start();
			
			new Thread(new Runnable() {
				public void run() {
					try {
						ObsUtil.getDataFromWeb(new HttpConnection(false), true);
						isTimeout = false;
						if (mTimeoutThread!=null) {
			        		mTimeoutThread.interrupt();
			        		mTimeoutThread=null;
			        	}
						forward();
					} catch (Exception e) {
						e.printStackTrace();
						forward();
						showToastFromBackground(ObsConst.VALUE_FAIL);
					}
				}
			}).start();
		} else {
			ThreadUtil.run(new Runnable() {
	            @Override
	            public void run() {
	                forward();
	            }
	         }, Const.DURATION_SPLASH);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void forward() {
		finish();
		Intent intent = null;
	    if (StringUtil.isNotEmpty(userId)) {
	    	intent = new Intent(SplashActivity.this, ObsBottomTabFragmentActivity.class);//BasicMapDemoActivity,ObsBottomTabFragmentActivity, CameraDemoActivity
		} else {
			intent = new Intent(SplashActivity.this, DriverLoginActivity.class);
		}
	    SplashActivity.this.startActivity(intent);
        transitForward();
	}
}
	
	