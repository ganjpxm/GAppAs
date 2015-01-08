/**
 * ObsActivity.java
 * OBSC Project
 *
 * Created by Gan Jianping on 13/06/13.
 * Copyright (c) 2013 DBS. All rights reserved.
 */
package com.onetransport.obs.common.activity;

import org.ganjp.glib.core.BaseActivity;
import com.onetransport.obs.R;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.other.ObsApplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>The activity will be extended by all the Activity</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public abstract class ObsActivity extends BaseActivity {
	
	//logo, call, about button in the header bar
	protected Button mBackBtn = null;
	protected TextView mTitleTv = null;
	protected Button mNextBtn = null;
	protected WebView mWebView = null;
	
	/**
	 * <p>Pop last activity and push current activity to the instance of DbsActivityUtil</p>
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHeaderButton();
	}
	
	/**
	 * <p>Click event : logo button, call button, info button</p>
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		super.onClick(view);
	}
	
	/**
	 * <p>Inflate the menu; this adds items to the action bar if it is present.</p>
	 *  
	 * @param menu 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId()==R.id.action_settings) {
			System.exit(0);
		}
		return super.onOptionsItemSelected(item);		
	}
	
	/**
	 * <p>Init all buttons in the header</p>
	 */
	protected void initHeaderButton() {
	    mBackBtn = (Button)findViewById(R.id.back_btn);
	    if (mBackBtn!=null) {
	    	mBackBtn.setOnClickListener(this);
		}
	    mTitleTv = (TextView)findViewById(R.id.title_tv);
	    mNextBtn = (Button)findViewById(R.id.next_btn);
	    if (mNextBtn!=null) {
	    	mNextBtn.setOnClickListener(this);
		}
	    mProgress = (ProgressBar) findViewById(R.id.progress);
        mWebView = (WebView)findViewById(R.id.web_view);
	}
	
	/**
	 * <p>Diable back button</p>
	 */
	@Override
	public void onBackPressed() {
		if (mWebView!=null && mWebView.canGoBack() == true){
			mWebView.goBack();
        } else {
        	super.onBackPressed();
        }
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
	
	public void showToastFromBackground(final String message) {
    	runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	if (message.equals(ObsConst.VALUE_FAIL)) {
					Toast.makeText(ObsApplication.getAppContext(), ObsApplication.getAppContext().getString(R.string.fail), Toast.LENGTH_SHORT).show();
				} else if (message.equals(ObsConst.VALUE_FAIL)) {
					Toast.makeText(ObsApplication.getAppContext(), ObsApplication.getAppContext().getString(R.string.success), Toast.LENGTH_SHORT).show();
				} else if (message.equals(ObsConst.VALUE_TIMEOUT)) {
					Toast.makeText(ObsApplication.getAppContext(), ObsApplication.getAppContext().getString(R.string.timeout), Toast.LENGTH_SHORT).show();
				}
	        }
    	});
    }
}	
