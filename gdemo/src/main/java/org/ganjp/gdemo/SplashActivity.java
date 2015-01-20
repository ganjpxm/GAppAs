/**
 * SplashActivity.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package org.ganjp.gdemo;

import android.content.Intent;
import android.os.Bundle;

import org.ganjp.gdemo.base.DemoActivity;
import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.ThreadUtil;

/**
 * <p>Splash screen activity</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class SplashActivity extends DemoActivity {
    private boolean mIsTimeout = false;
    private Thread mTimeoutThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if (NetworkUtil.isNetworkAvailable(this)) {
			mTimeoutThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
                mIsTimeout = true;
                try {
                    Thread.sleep(Const.TIMEOUT_CONNECT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mIsTimeout) {
                    forward();
                    showToastFromBackground(Const.VALUE_TIMEOUT);
                }
	            }
	        });
			mTimeoutThread.start();
			
			new Thread(new Runnable() {
				public void run() {
                try {
                    mIsTimeout = false;
                    if (mTimeoutThread!=null) {
                        mTimeoutThread.interrupt();
                        mTimeoutThread=null;
                    }
                    Thread.sleep(Const.DURATION_SPLASH);
                    forward();
                } catch (Exception e) {
                    e.printStackTrace();
                    forward();
                    showToastFromBackground(Const.VALUE_FAIL);
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
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        transitForward();
        finish();
	}
}
	
	