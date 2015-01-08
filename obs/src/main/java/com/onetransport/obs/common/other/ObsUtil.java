/**
 * Copyright (c) Gan Jianping, 2013. All rights reserved.
 * Author: GanJianping
 */
package com.onetransport.obs.common.other;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.StringUtil;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.dao.ObmBookingVehicleItemDAO;
import com.onetransport.obs.common.entity.ObmBookingVehicleItem;
import org.json.JSONObject;


public abstract class ObsUtil {
	
	/**
	 * <p>getDataFromJWeb</p>
	 * 
	 * @param h
	 * @param isUpdate
	 * @throws Exception
	 */
	public static void getDataFromWeb(HttpConnection h, boolean isUpdate) throws Exception {
//		//get bmConfigs
//		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
//		pairs.add(new BasicNameValuePair(ObsConst.KEY_CONFIG_CDS, ));
//		if (isUpdate) {
//			pairs.add(new BasicNameValuePair(ObsConst.KEY_LAST_TIME, String.valueOf(PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_CONFIG_LAST_TIME))));
//		}
//		h.post(ObsConst.URL_GET_BM_CONFIGS, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
//		String jsonData = HttpConnection.processEntity(h.getResponse().getEntity());
//		if (jsonData.startsWith("[") && !jsonData.equals("[]")) {
//			ObjectMapper mapper = new ObjectMapper();
//			BmConfig[] bmConfigs = mapper.readValue(jsonData, BmConfig[].class);
//			long lastTime = BmConfigDAO.getInstance().insertOrUpdate(bmConfigs);
//			PreferenceUtil.saveLong(ObsConst.KEY_PREFERENCE_CONFIG_LAST_TIME, lastTime);
//		}
		
		getBookingVehicleItemsFromWeb(h, isUpdate);
	}
	
	public static ObmBookingVehicleItem[] getBookingVehicleItemsFromWeb(HttpConnection h, boolean isUpdate) throws Exception {
		ObmBookingVehicleItem[] obmBookingVehicleItems = null;
		String driverUserId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBSD);
		String startDate = DateUtil.getDdMmYYYYHhMmSsFormate(PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_ITEM_LAST_TIME));
		String url = ObsConst.URL_GET_DRIVER_BOOKING+driverUserId;
		if (isUpdate) {
			url += "?" + ObsConst.KEY_START_DATE + "=" + java.net.URLEncoder.encode(startDate);
		} else {
			PreferenceUtil.saveLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_ITEM_LAST_TIME, 0);
		}
		h.get(url);
		if (h.getResponse()!=null) {
			String jsonData = HttpConnection.processEntity(h.getResponse().getEntity());
			JSONObject jsonObject = new JSONObject(jsonData);
			String data = jsonObject.getString("data");
			if (StringUtil.isNotEmpty(data) && !"[]".equalsIgnoreCase(data)) {
		    	ObjectMapper mapper = new ObjectMapper();
		    	obmBookingVehicleItems = mapper.readValue(data, ObmBookingVehicleItem[].class);
				long lastTime = ObmBookingVehicleItemDAO.getInstance().insertOrUpdate(obmBookingVehicleItems);
				PreferenceUtil.saveLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_ITEM_LAST_TIME, lastTime);
			}
		}
		return obmBookingVehicleItems;
	}
	
	public static String acceptBooking(String bookingVehicleItemId) {
//		NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
//        NSString *userCd = [JpDataUtil getValueFromUDByKey:KEY_USER_CD_OBSD];
//        NSDictionary *userDic = [JpDataUtil getDicFromUDByKey:userCd];
//        [parameters setObject:[userDic objectForKey:KEY_USER_ID_OBSD] forKey:@"userId"];
//        [parameters setObject:[userDic objectForKey:KEY_USER_NAME_OBSD] forKey:@"userName"];
//        [parameters setObject:[userDic objectForKey:KEY_USER_MOBILE_PHONE_OBSD] forKey:@"userName"];
//        [parameters setObject:[userDic objectForKey:KEY_USER_EXTEND_ITEMS_OBSD] forKey:@"vehicle"];
//        [parameters setObject:[JpDataUtil getValueFromUDByKey:KEY_BOOKING_VEHICLE_ITEM_ID_OBSD] forKey:@"bookingVehicleItemId"];
		try {
			String userCd = PreferenceUtil.getString(ObsConst.KEY_USER_CD_OBSD);
	        if (StringUtil.isNotEmpty(userCd)) {
	       	 	String jsonData = PreferenceUtil.getString(userCd);
	       	 	if (StringUtil.isNotEmpty(jsonData)) {
		       	 	JSONObject jsonObject = new JSONObject(jsonData);
			       	HttpConnection httpConnection = new HttpConnection(false);
			 		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			 		pairs.add(new BasicNameValuePair("bookingVehicleItemId", bookingVehicleItemId));
			 		pairs.add(new BasicNameValuePair("userId", jsonObject.getString(ObsConst.KEY_USER_ID_OBSD)));
			 		pairs.add(new BasicNameValuePair("userName", jsonObject.getString(ObsConst.KEY_USER_NAME_OBSD)));
			 		pairs.add(new BasicNameValuePair("mobileNumber", jsonObject.getString(ObsConst.KEY_USER_MOBILE_PHONE_OBSD)));
			 		pairs.add(new BasicNameValuePair("vehicle", jsonObject.getString(ObsConst.KEY_USER_EXTEND_ITEMS_OBSD)));
			 		httpConnection.post(ObsConst.URL_ACCEPT_BOOKING, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
					String responseJsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
					if (StringUtil.isNotEmpty(responseJsonData)) {
						JSONObject responseJsonObject = new JSONObject(responseJsonData);
				    	//result:success/error
				    	String result = responseJsonObject.getString("result");
				    	if (ObsConst.VALUE_SCCESS.equalsIgnoreCase(result)) {
				    		return "Thank you, booking added.";
				    	} else if (ObsConst.VALUE_ACCEPTED.equalsIgnoreCase(result)) {
				    		return "Sorry, job taken!";
				    	} else {
				    		return "Sorry, system error.";
				    	}
					}
	       	 	}
	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        
		return "Accept Fail";
	}

	/**
	 * <p>Regist device to server</p>
	 * 
	 * @param regId PreferenceUtil.getString(ObsConst.KEY_REG_ID_OBSD);
	 * @param state ObsConst.VALUE_NO
	 * @return ObsConst.VALUE_FAIL ObsConst.VALUE_SUCCESS 
	 */
	public static String registDevice(String regId, String state) {
		String result = ObsConst.VALUE_FAIL;
		try {
			HttpConnection httpConnection = new HttpConnection(false);
	        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBSD)));
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_PLATFORM, "android"));
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_USE_PUSH_NOTIFICATION, state));
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_DEVICE_TOKEN, regId));
	    	httpConnection.post(ObsConst.URL_REGISTE_DEVICE, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
	    	String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
	    	JSONObject jsonObject = new JSONObject(jsonData);
	    	//result:success/error
	    	result = jsonObject.getString("result");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	return result;
	}
}