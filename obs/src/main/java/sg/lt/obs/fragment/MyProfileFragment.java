package sg.lt.obs.fragment;

import java.util.HashMap;
import java.util.Map;

import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.WebViewUtil;
import sg.lt.obs.R;
import sg.lt.obs.common.ObsConst;
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

public class MyProfileFragment extends Fragment {

	private View mParent;
	private FragmentActivity mActivity;
	private TitleView mTitleView;
	protected WebView mWebView;
	
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
        mWebView.loadUrl("file:///android_asset/www/page/obsdMyProfile.html");
	}
	
	private void goSignInActivity() {
//		Intent intent = new Intent(mActivity, HelpActivity.class);
//		startActivity(intent);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
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
        	DialogUtil.showLoadingDialog(mActivity);
        }
        
        @Override
        public void onPageFinished(WebView webView, String url) {
        	DialogUtil.dismissProgressDialog();
        	super.onPageFinished(webView, url);
        	webView.clearCache(true);
        	
    		//set person name
        	try {
	        	JSONObject jsonObject = null;
	        	String userCd = PreferenceUtil.getString(ObsConst.KEY_USER_CD_OBS);
	        	if (StringUtil.isNotEmpty(userCd)) {
	        		String jsonData = PreferenceUtil.getString(userCd);
	        		if (StringUtil.isNotEmpty(jsonData)) {
	        			jsonObject = new JSONObject(jsonData);
	        		}
	        	}
	        	if (jsonObject!=null) {
	        		StringBuffer jsSb = new StringBuffer("javascript:(function() { ");
	        		jsSb.append("document.getElementById('myName').innerHTML='").append(jsonObject.getString(ObsConst.KEY_USER_NAME_OBS)).append("';");
	        		jsSb.append("document.getElementById('mobileNumber').innerHTML='").append(jsonObject.getString(ObsConst.KEY_USER_MOBILE_NUMBER_OBS)).append("';");
	        		jsSb.append("document.getElementById('email').innerHTML='").append(jsonObject.getString(ObsConst.KEY_USER_EMAIL_OBS)).append("';");
	        		//vehicleMake:Mercedes Benz\t;vehicleModel:Viano;vehicleNo:PC1206L;vehicleColor:Black
	        		//ObsdUserExtendItems
	        		String userExtendItems = jsonObject.getString("ObsdUserExtendItems");
	        		if (StringUtil.isNotEmpty(userExtendItems) && !jsonObject.has("vehicleMake")) {
	        			String[] arry1 = userExtendItems.split(";");
	        			for (String str : arry1) {
	        				String[] arry2 =  str.split(":");
	        				if (arry2[0].equalsIgnoreCase("vehicleMake")) {
	        					jsSb.append("document.getElementById('make').innerHTML='").append(arry2[1]).append("';");
	        				} else if (arry2[0].equalsIgnoreCase("vehicleModel")) {
	        					jsSb.append("document.getElementById('model').innerHTML='").append(arry2[1]).append("';");
	        				} else if (arry2[0].equalsIgnoreCase("vehicleNo")) {
	        					jsSb.append("document.getElementById('vehicleNumber').innerHTML='").append(arry2[1]).append("';");
	        				} else if (arry2[0].equalsIgnoreCase("vehicleColor")) {
	        					jsSb.append("document.getElementById('color').innerHTML='").append(arry2[1]).append("';");
	        				}
	        			}
	        		} else {
	        			jsSb.append("document.getElementById('make').innerHTML='").append(jsonObject.getString("vehicleMake")).append("';");
		        		jsSb.append("document.getElementById('model').innerHTML='").append(jsonObject.getString("vehicleModel")).append("';");
		        		jsSb.append("document.getElementById('vehicleNumber').innerHTML='").append(jsonObject.getString("vehicleNo")).append("';");
		        		jsSb.append("document.getElementById('color').innerHTML='").append(jsonObject.getString("vehicleColor")).append("';");
	        		}
	        		jsSb.append("})()");
	        		webView.loadUrl(jsSb.toString());
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
