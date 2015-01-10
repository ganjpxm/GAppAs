/**
 * HttpConnection.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.ganjp.glib.core.Const;

import android.util.Log;

/**
 * <p>Htpp Connection</p>
 *
 * @author GanJianping
 * @since 1.0.0
 */
public class HttpConnection extends ConnectionUtil implements Runnable {

	public static final int DID_START = 0;
	public static final int DID_ERROR = 1;
	public static final int DID_SUCCEED = 2;

	public static final String RESP_START = "Start";
	public static final String RESP_ERROR = "Error";
	public static final String RESP_SUCCEED = "Succeed";

	private static final int GET = 0;
	private static final int POST = 1;
	private static final int PUT = 2;
	private static final int DELETE = 3;

	private String url;
	private int method;
	private boolean runnable = false;
	private UrlEncodedFormEntity data;
	private HttpResponse response = null;
	private String responseMsg = null;
	
	private DefaultHttpClient httpClient;

	public HttpConnection(boolean runnable) {
		this.runnable = runnable;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public HttpResponse getResponse() {
		return this.response;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getResponseMsg() {
		return this.responseMsg;
	}

	public void get(String url) {
		create(GET, url, null);
	}
	
	public void post(String url, UrlEncodedFormEntity data) {
		create(POST, url, data);
	}

	public void put(String url, UrlEncodedFormEntity data) {
		create(PUT, url, data);
	}

	public void delete(String url) {
		create(DELETE, url, null);
	}

	private void create(int method, String url, UrlEncodedFormEntity data) {
		this.method = method;
		this.url = url;
		this.data = data;
		httpClient = getThreadSafeClient();
		if (runnable) {
			ConnectionManager.getInstance().push(this);
		} else {
			processConn();
		}
	}

	private synchronized DefaultHttpClient getThreadSafeClient() {
		if (httpClient != null) {
            return httpClient;
        }
		httpClient = new DefaultHttpClient();
		ClientConnectionManager mgr = httpClient.getConnectionManager();
		HttpParams params = httpClient.getParams();
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
		return httpClient;
	}

	private void processConn() {
		setResponseMsg(HttpConnection.RESP_START);
		handleMessage(HttpConnection.DID_START);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), Const.TIMEOUT_CONNECT);
		setResponse(null);
		int tries = 0;
		do {
			try {
				tries++;
				switch (method) {
				case GET:
					setResponse(httpClient.execute(new HttpGet(url)));
					break;
				case POST:
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(data);
					setResponse(httpClient.execute(httpPost));
					break;
				case PUT:
					HttpPut httpPut = new HttpPut(url);
					httpPut.setEntity(data);
					setResponse(httpClient.execute(httpPut));
					break;
				case DELETE:
					setResponse(httpClient.execute(new HttpDelete(url)));
					break;
				}
				setResponseMsg(HttpConnection.RESP_SUCCEED);
				handleMessage(HttpConnection.DID_SUCCEED);
				tries = 1;
			} catch (Exception e) {
				Log.w(getClass().toString(), "HttpConnection.processConn()::Exception..." + e.getMessage() + "..." + e.toString() + url);
				setResponseMsg(HttpConnection.RESP_ERROR + "-" + e.getMessage());
				handleMessage(HttpConnection.DID_ERROR);
			}
		} while (tries != 1);
	}

	public void run() {
		processConn();
		ConnectionManager.getInstance().didComplete(this);
	}

	/**
	 * Handle the message during the connection process (Noted: you may override
	 * this method if need any further execution for this method)
	 * 
	 * @param what
	 */
	public void handleMessage(int what) {
		switch (what) {
		case HttpConnection.DID_START: {
			Log.i(getClass().toString(), "Starting connection..." + url);
			break;
		}
		case HttpConnection.DID_SUCCEED: {
			Log.i(getClass().toString(), "Response succeed!" + url);
			break;
		}
		case HttpConnection.DID_ERROR: {
			Log.w(getClass().toString(), "Connection failed!" + url);
			break;
		}
		}
	}
}
