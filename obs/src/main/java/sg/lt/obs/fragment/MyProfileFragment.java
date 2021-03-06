package sg.lt.obs.fragment;

import java.util.HashMap;
import java.util.Map;

import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.WebViewUtil;
import sg.lt.obs.R;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.common.view.TitleView;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyProfileFragment extends Fragment {

	private View mParent;
	private FragmentActivity mActivity;
	private TitleView mTitleView;
	protected WebView mWebView;
    private boolean isReload = true;
	
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at 'index'.
	 */
	public static MyProfileFragment newInstance(int index) {
		MyProfileFragment f = new MyProfileFragment();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mParent = getView();

		mTitleView = (TitleView) mParent.findViewById(R.id.title);
		mTitleView.setTitle(R.string.my_profile_title);
		mTitleView.hiddenLeftButton();
		mTitleView.hiddenRightButton();
		mWebView = (WebView)mParent.findViewById(R.id.web_view);
		
		WebViewUtil.initWebView(mWebView);
        //It will display error info if network is unavailable
    	Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
        noCacheHeaders.put("Pragma", "no-cache");
        noCacheHeaders.put("Cache-Control", "no-cache");
        mWebView.setWebViewClient(new WebViewClientEx(mActivity));
        mWebView.setWebChromeClient(new WebChromeClientEx());
	}
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
        if (hidden==false && isReload==true) {
            mWebView.loadUrl(ObsConst.URL_GET_DRIVER_PROFILE + PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS));
            isReload = false;
        }
        if (hidden==false) {
            Tracker t = ((ObsApplication) mActivity.getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
            t.setScreenName("My Profile");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
	}

    @Override
    public void onStop() {
        super.onStop();
        isReload = true;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

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
            DialogUtil.showProcessingDialog(mActivity);
        }
        
        @Override
        public void onPageFinished(WebView webView, String url) {
        	DialogUtil.dismissProgressDialog();
        	super.onPageFinished(webView, url);
        	webView.clearCache(true);
        	
    		//set person name
        	try {
                //StringBuffer jsSb = new StringBuffer("javascript:(function() { ");
                //jsSb.append("document.getElementById('myName').innerHTML='").append(jsonObject.getString(ObsConst.KEY_USER_NAME_OBS)).append("';");
                //jsSb.append("})()");
                //webView.loadUrl(jsSb.toString());
                if (StringUtil.hasText(url) && url.indexOf("editDriverVehicle")!=-1) {
                    mTitleView.setTitle(R.string.edit_profile);
                    mTitleView.setLeftButton(R.string.back, new TitleView.OnLeftButtonClickListener() {
                        @Override
                        public void onClick(View button) {
                            if (mWebView.canGoBack()) {
                                mWebView.goBack();
                                mTitleView.removeLeftButton();
                            }
                        }
                    });
                } else {
                    mTitleView.hiddenLeftButton();
                    mTitleView.setTitle(R.string.my_profile_title);
                }
        	} catch(Exception ex) {
        		ex.printStackTrace();
        	}
        }
        
        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
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
    }
    
    /**
     * <p>Solve alert and prompt dialog bug</p>
     */
    class WebChromeClientEx extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        	DialogUtil.showAlertDialog(mActivity, android.R.drawable.ic_dialog_alert, getString(R.string.alert), message, 
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
        	 DialogUtil.showAlertDialog(mActivity, android.R.drawable.ic_dialog_alert, getString(R.string.confirm), message, 
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
            final LayoutInflater factory = LayoutInflater.from(mActivity);
            final View v = factory.inflate(R.layout.common_javascript_prompt_dialog, null);
            ((TextView)v.findViewById(R.id.prompt_message_text)).setText(message);
            ((EditText)v.findViewById(R.id.prompt_input_field)).setText(defaultValue);

            DialogUtil.showCustomeDialog(mActivity, android.R.drawable.ic_dialog_info, null, view, 
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
            super.onReceivedTitle(view, title);
        }
    }
}
