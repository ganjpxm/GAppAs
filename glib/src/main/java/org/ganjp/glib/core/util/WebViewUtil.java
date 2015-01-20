/**
 * WebViewUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import org.ganjp.glib.R;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <p>Web View Utility</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class WebViewUtil {

	/**
	 * <p>Set webview</p>
	 * 
	 * @param webView
	 */
	public static void initWebView(WebView webView) {
        WebSettings tcWs = webView.getSettings();
        tcWs.setJavaScriptEnabled(true);
        tcWs.setSupportZoom(true);
	}
	
	/**
	 * <p>Init Browser</p>
	 * 
	 * @param webView
	 */
	public static void initBrowser(WebView webView) {
        WebSettings tcWs = webView.getSettings();
        tcWs.setJavaScriptEnabled(true);
        tcWs.setUseWideViewPort(true);
        tcWs.setLoadWithOverviewMode(true);
        tcWs.setSupportZoom(true);
        tcWs.setBuiltInZoomControls(true);
//      tcWs.setSupportMultipleWindows(true);
	}
	
	/**
	 * <p>Show error information</p>
	 * 
	 * @param errorInfo
	 * @param webView
	 */
	public static void showErrorInfo(String errorInfo, WebView webView) {
		webView.loadData(HtmlUtil.getErrorHtml(errorInfo), "text/html", "utf-8");
	}
	
	/**
	 * <p>Display error information in onReceivedError</p>
	 * 
	 * @param context
	 * @param webView
	 * @param errorCode
	 * @param description
	 */
	public static void doError(Context context, WebView webView, int errorCode, String description) {
		Log.e("Webview", "Load page error : code : " + errorCode + " Desc : " + description);
		if (errorCode==WebViewClient.ERROR_CONNECT || errorCode==WebViewClient.ERROR_HOST_LOOKUP) {
			showErrorInfo(context.getString(R.string.error_connect_or_no_network), webView);
		} else {
			showErrorInfo(description, webView);
		}
	}
	
	/**
	 * <p>Go back</p>
	 * 
	 * @param keyCode
	 * @param webView
	 * @return
	 */
	public static boolean goBack(int keyCode, WebView webView) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
            return true;
        } else {
        	return false;
        }
	}
	
}
