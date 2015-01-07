/**
 * BaseActivity.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package org.ganjp.glib.core;

import org.ganjp.glib.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * <p>Base Activity</p>
 * 
 * @author GanJianping
 * @since v1.0.0
 */
public abstract class BaseActivity extends Activity implements OnClickListener {
	
	protected ProgressDialog mProgressDialog;
	
	/**
	 * Called when the activity is first created
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * <p>Called after your activity has been stopped, prior to it being started again.</p>
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	/**
	 * <p>Called when the activity is becoming visible to the user.</p>
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * <p>Called when the activity will start interacting with the user.</p>
	 */
	@Override
	protected void onResume() {
        super.onResume();  
	}
	
	/**
	 * <p>Called when the system is about to start resuming a previous activity.</p>
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	/**
	 * <p>Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.</p>
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	/**
	 * <p>The final call you receive before your activity is destroyed. This can happen either because the activity is finishing.</p>
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * <p>Click Button Event</p>
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		Log.i("BaseActivity", "onClick:"+view.getId());
	}

	/**
	 * refresh current activity
	 */
	protected void refresh() {
		finish();
		startActivity(getIntent());
	}

	/**
	 * Log info
	 * 
	 * @param msg
	 */
	protected void log(String msg) {
		Log.i(this.getClass().getName(), msg);
	}
	
	/**
	 * Get current activity context
	 * 
	 * @return
	 */
	protected Context getContext() {
		return this;
	}
	
	/**
	 * <p>Get screen width</p>
	 * 
	 * @return int
	 */
	protected int getScreenWidth() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}
	
	/**
	 * <p>Get screen height</p>
	 * 
	 * @return
	 */
	protected int getScreenHeight() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	/**
	 * <p>Transit forward</p>
	 */
	protected void transitForward() {
		transitSlideLeft();
	}
	
	/**
	 * <p>Transit back</p>
	 */
	protected void transitBack() {
		transitSlideRight();
	}
	
	/**
	 * <p>slide from left to right</p>
	 */
	protected void transitSlideLeft() {
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	/**
	 * <p>Slide from right to left</p>
	 */
	protected void transitSlideRight() {
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	/**
	 * <p>Slide from up to down</p>
	 */
	protected void transitSlideDown() {
		overridePendingTransition(0, R.anim.slide_up);
	}
	
	/**
	 * <p>Slide from down to up</p>
	 */
	protected void transitSlideUp() {
		overridePendingTransition(0, R.anim.slide_down);
	}
	
	/**
	 * <p>Flip from shrink to grow</p>
	 */
	protected void transitShrinkGrow() {
		overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
	}
	
	protected static ProgressBar mProgress;
	protected static void showProgressBar() {
		if (mProgress!=null) {
			mProgress.setVisibility(TextView.VISIBLE);
		}
	}
	
	protected static void dismissProgressBar() {
		if (mProgress!=null) {
			mProgress.setVisibility(TextView.GONE);
		}
	}
}	
