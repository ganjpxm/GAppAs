package sg.lt.obs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.WebViewUtil;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class DriverLoginActivity extends ObsActivity {

	private boolean mHasReload = false;
	private boolean mIsLoadingUrl = true;
	private Timer mTimer;
	private static Thread mTimeoutThread;
	boolean isTimeout = false;
    private Map<String,String> mResultMap;
	/**
	 * Init view and load application form from pweb   
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Tracker t = ((ObsApplication) getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Driver Login");
        t.send(new HitBuilders.AppViewBuilder().build());
        
        mNextBtn.setVisibility(View.INVISIBLE);
        mBackBtn.setVisibility(View.INVISIBLE);

        WebViewUtil.initWebView(mWebView);
        //It will display error info if network is unavailable
    	Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
        noCacheHeaders.put("Pragma", "no-cache");
        noCacheHeaders.put("Cache-Control", "no-cache");
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.setWebViewClient(new WebViewClientEx(this));
        mWebView.setWebChromeClient(new WebChromeClientEx());
        mWebView.loadUrl("file:///android_asset/www/page/driverLogin.html");
    }
 
    /**
     * Button click event
     */
    @Override
	public void onClick(View view) {
		
    }
    
    /**
     *  <p>WebView will display timeout info if it load timeout and it will display error info if it load error.</p>
     *  <pre>
     *  	mWebView.setWebViewClient(new WebViewClientEx());
     *  </pre> 
     */
    class WebViewClientEx extends WebViewClient {
    	boolean timeout = true;
    	String mUrl = "";
    	Context context;

    	/**
         * <p>Instantiate the WebViewClient and set the context</p>
         * 
         * @param ctx
         */
    	WebViewClientEx(Context ctx) {
    		context = ctx;
        }
    	
    	/**
    	 * <p>It will display the timeout info if webView load timeout</p>
    	 */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	DialogUtil.showLoadingDialog(getContext());
        	timeout = true;
            mUrl = url;
			super.onPageStarted(view, url, favicon);
			if (mIsLoadingUrl) {
				mTimeoutThread = new Thread(new Runnable() {
    	            @Override
    	            public void run() {
    	                try {
    	                	Thread.sleep(Const.TIMEOUT_CONNECT);
    	                } catch (InterruptedException e) {
    	                    e.printStackTrace();
    	                }
    	                if(timeout) {
    	                	DialogUtil.dismissProgressDialog();
    	                	mHandlerDialog.obtainMessage(0).sendToTarget();
    	                }
    	            }
    	        });
				mTimeoutThread.start();//runOnUiThread(mTimeoutThread)
			} else {
				mIsLoadingUrl = true;
			}
        }
        
        @Override
        public void onPageFinished(WebView webView, String url) {
        	DialogUtil.dismissProgressDialog();
        	timeout = false;
        	if (mTimeoutThread!=null) {
        		mTimeoutThread.interrupt();
        		mTimeoutThread=null;
        	}
        	super.onPageFinished(webView, url);
        	webView.clearCache(true);
			
			mHasReload = true;
        }
        
        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
        	mHasReload = true;
        	DialogUtil.dismissProgressDialog();
        	WebViewUtil.doError(context, webView, errorCode, description);
        }
        
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        	if (ObsConst.IGNORE_SSL) {
        		handler.proceed(); // this will ignore the Ssl error and will go forward to your site
	        } else {
	        	super.onReceivedSslError(view, handler, error);
	        	WebViewUtil.doError(context, view, error.getPrimaryError(), "SSL Error");
	        }
        }
    }
    
    /**
     * <p>Interactive with web view by js call WebAppInterface method</p>
     * <pre>
     * mWebView.getSettings().setJavaScriptEnabled(true); 
     * mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
     * webView.loadUrl("javascript:window.Android.setLoanInfo(document.getElementById('applicationNo').value);");
     * </pre>
     * 
     */
    private final class WebAppInterface {
    	Context mContext;

        /**
         * <p>Instantiate the interface and set the context</p>
         * 
         * @param ctx
         */
    	WebAppInterface(Context ctx) {
    		mContext = ctx;
        }
        
    	/**
    	 *<p>Show a toast from the web page</p>
    	 * 
    	 * @param toast
    	 */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        
        /**
         * <p>Launch another activity</p>
         * 
         * @param mobileNumber
         * @param password
         */
        @JavascriptInterface
        public void login(final String mobileNumber, final String password) {
        	Activity activity = (Activity)mContext;
	        if (NetworkUtil.isNetworkAvailable(activity)) {
	        	DialogUtil.showProcessingDialog(mContext);
	        	isTimeout = true;
	        	mTimeoutThread = new Thread(new Runnable() {
    	            @Override
    	            public void run() {
    	                try {
    	                	Thread.sleep(Const.TIMEOUT_CONNECT);
    	                } catch (InterruptedException e) {
    	                    e.printStackTrace();
    	                }
    	                if (isTimeout) {
	    	        		mHandlerDialog.obtainMessage(0).sendToTarget();
    	                }
    	               }
	    	    });
				mTimeoutThread.start();
		        new Thread(new Runnable() {
					public void run() {
                        try {
                            HttpConnection httpConnection = new HttpConnection(false);
                            ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                            pairs.add(new BasicNameValuePair(ObsConst.KEY_LOGIN_USER_CD_OR_EMAIL_OR_MOBILE_NUMBER, mobileNumber));
                            pairs.add(new BasicNameValuePair(ObsConst.KEY_LOGIN_USER_PASSWORD, password));
                            httpConnection.post(ObsConst.URL_LOGIN, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                            String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                            isTimeout = false;
                            if (mTimeoutThread!=null) {
                                mTimeoutThread.interrupt();
                                mTimeoutThread=null;
                            }
                            JSONObject jsonObject = new JSONObject(jsonData);
                            //result:success/error
                            String result = jsonObject.getString("result");
                            if ("success".equalsIgnoreCase(result)) {
                                String userId = jsonObject.getString(ObsConst.KEY_USER_ID_OBS);
                                PreferenceUtil.saveString(ObsConst.KEY_USER_ID_OBS, userId);
                                PreferenceUtil.saveString(userId, jsonData);

                                //Get driver booking information
                                httpConnection.get(ObsConst.URL_GET_DRIVER_BOOKING + userId);
                                mResultMap = ObsUtil.getDataFromWeb(new HttpConnection(false), false);
                                forward();
                            } else {
                                DialogUtil.dismissProgressDialog();
                                mHandlerDialog.obtainMessage(1).sendToTarget();
                            }
                        } catch (Exception e) {
                            log(e.getMessage());
                            isTimeout = false;
                            if (mTimeoutThread!=null) {
                                mTimeoutThread.interrupt();
                                mTimeoutThread=null;
                            }
                            DialogUtil.dismissProgressDialog();
                            mHandlerDialog.obtainMessage(2).sendToTarget();
                        }
					}
				}).start();
        	} else {
                DialogUtil.dismissProgressDialog();
        		DialogUtil.showAlertDialog(mContext, getString(R.string.error_no_network));
        	}
        }
    }
    
    private Handler mHandlerDialog = new Handler() {  
    	public void handleMessage (Message msg) { 
        if (msg.what==0) {
            mHasReload = true;
            mIsLoadingUrl = false;
            DialogUtil.showAlertDialog(DriverLoginActivity.this, getString(R.string.error_loading_timeout));
        } else if (msg.what==1) {
            DialogUtil.showAlertDialog(DriverLoginActivity.this, getString(R.string.error_login_fail));
        } else if (msg.what==2) {
            DialogUtil.showAlertDialog(DriverLoginActivity.this, getString(R.string.error_connect_fail));
        }
    	}  
    }; 
    
    /**
     * <p>Solve alert and prompt dialog bug</p>
     */
    class WebChromeClientEx extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        	getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        	DialogUtil.showAlertDialog(DriverLoginActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.alert), message, 
        			new DialogInterface.OnClickListener() {
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        		if (which==-1) result.confirm();
	        		
	            }
        	}, getString(R.string.ok));
            
            return true;
        };

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        	 DialogUtil.showAlertDialog(DriverLoginActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.confirm), message, 
        				new DialogInterface.OnClickListener() {
        		        	@Override
        		            public void onClick(DialogInterface dialog, int which) {
        		        		if (which==-1) result.confirm();
        		        		else if (which==-2) result.cancel();
        		            }
        	       }, new String[]{getString(R.string.confirm), getString(R.string.cancel)});
            return true;
        };

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            final LayoutInflater factory = LayoutInflater.from(DriverLoginActivity.this);
            final View v = factory.inflate(org.ganjp.glib.R.layout.common_javascript_prompt_dialog, null);
            ((TextView)v.findViewById(R.id.prompt_message_text)).setText(message);
            ((EditText)v.findViewById(R.id.prompt_input_field)).setText(defaultValue);

            DialogUtil.showCustomeDialog(DriverLoginActivity.this, android.R.drawable.ic_dialog_info, null, view, 
    			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
			    	if (which==-1) {
			    		String value = ((EditText)v.findViewById(R.id.prompt_input_field)).getText().toString();
                        result.confirm(value);
			    	} else if (which==-2) {
			    		result.cancel();
			    	}
			    }
    		});
            return true;
        };
        
        @Override
        public void onReceivedTitle(WebView view, String title) {
            setTitle(title);
            super.onReceivedTitle(view, title);
        }
    }
    
    private void forward() {
        Intent intent = new Intent(DriverLoginActivity.this, ObsBottomTabFragmentActivity.class);
//        if (mResultMap !=null && StringUtil.hasText(mResultMap.get(ObsConst.KEY_BROADCAST_BOOKING_VEHICLE_ITEM_IDS))) {
//            intent = new Intent(DriverLoginActivity.this, BookingVehicleAlarmListActivity.class);
//        } else {
//            intent = new Intent(DriverLoginActivity.this, ObsBottomTabFragmentActivity.class);
//        }
    	DriverLoginActivity.this.startActivity(intent);
    	transitForward();	
    }
}
