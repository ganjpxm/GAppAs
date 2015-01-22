/**
 * Copyright (c) Gan Jianping, 2013. All rights reserved.
 * Author: GanJianping
 */
package sg.lt.obs.common.other;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.base.Const;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import org.json.JSONObject;

public abstract class ObsUtil {

    public static Location sCurrentLocation;
	/**
	 * <p>getDataFromWeb</p>
	 * 
	 * @param pHttpConnection
	 * @param pIsUpdate
	 * @throws Exception
	 */
	public static Map<String,String> getDataFromWeb(HttpConnection pHttpConnection, boolean pIsUpdate) throws Exception {
//		//get bmConfigs
//		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
//		pairs.add(new BasicNameValuePair(ObsConst.KEY_CONFIG_CDS, ));
//		if (pIsUpdate) {
//			pairs.add(new BasicNameValuePair(ObsConst.KEY_LAST_TIME, String.valueOf(PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_CONFIG_LAST_TIME))));
//		}
//		pHttpConnection.post(ObsConst.URL_GET_BM_CONFIGS, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
//		String jsonData = HttpConnection.processEntity(pHttpConnection.getResponse().getEntity());
//		if (jsonData.startsWith("[") && !jsonData.equals("[]")) {
//			ObjectMapper mapper = new ObjectMapper();
//			BmConfig[] bmConfigs = mapper.readValue(jsonData, BmConfig[].class);
//			long lastTime = BmConfigDAO.getInstance().insertOrUpdate(bmConfigs);
//			PreferenceUtil.saveLong(ObsConst.KEY_PREFERENCE_CONFIG_LAST_TIME, lastTime);
//		}
		
		return getBookingVehicleItemsFromWeb(pHttpConnection, pIsUpdate);
	}

    /**
     * <p>get obmBookingVehicleItems data from obs server</p>
     *
     * @param pHttpConnection
     * @param pIsUpdate
     * @return
     * @throws Exception
     */
	public static Map<String,String> getBookingVehicleItemsFromWeb(HttpConnection pHttpConnection, boolean pIsUpdate) throws Exception {
        Map<String,String> resultMap = new HashMap<String,String>();
		ObmBookingVehicleItem[] obmBookingVehicleItems = null;
		String driverUserId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
		String url = ObsConst.URL_GET_DRIVER_BOOKING + driverUserId;
        String bookingVehicleItemLastUpdateDatetime = "";
		if (pIsUpdate) {
            bookingVehicleItemLastUpdateDatetime = DateUtil.getDdMmYYYYHhMmSsFormate(PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_UPDATE_ITEM_LAST_TIME));
			if (StringUtil.hasText(bookingVehicleItemLastUpdateDatetime)) {
                url += "?" + ObsConst.KEY_START_DATE + "=" + java.net.URLEncoder.encode(bookingVehicleItemLastUpdateDatetime);
            }
		}
        System.out.println(url);
		pHttpConnection.get(url);
		if (pHttpConnection.getResponse()!=null) {
			String jsonData = HttpConnection.processEntity(pHttpConnection.getResponse().getEntity());
			JSONObject jsonObject = new JSONObject(jsonData);
			String data = jsonObject.getString("data");
            String broadcastBookingVehicleItemIds = jsonObject.getString("broadcastBookingVehicleItemIds");
            resultMap.put("broadcastBookingVehicleItemIds", broadcastBookingVehicleItemIds);
			if (StringUtil.hasText(data) && !"[]".equalsIgnoreCase(data)) {
		    	ObjectMapper mapper = new ObjectMapper();
		    	obmBookingVehicleItems = mapper.readValue(data, ObmBookingVehicleItem[].class);
                resultMap.put("updateSize", String.valueOf(obmBookingVehicleItems.length));
                if (pIsUpdate==false) {
                    ObmBookingVehicleItemDAO.getInstance().delete();
                }
				long lastTime = ObmBookingVehicleItemDAO.getInstance().insertOrUpdate(obmBookingVehicleItems);
				PreferenceUtil.saveLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_UPDATE_ITEM_LAST_TIME, lastTime);
                if (StringUtil.hasText(bookingVehicleItemLastUpdateDatetime) && pIsUpdate) {
                    List<Map<String,String>> maps = ObmBookingVehicleItemDAO.getInstance().getAllBookingVehicleItem();
                    int itemSize = 0;
                    for (Map<String,String> map : maps) {
                        String bookingVehicleItemId = map.get(ObmBookingVehicleItemDAO.COLUMN_BOOKING_VEHICLE_ITEM_ID);
                        if ("yes".equals(map.get(ObmBookingVehicleItemDAO.COLUMN_BROADCAST_TAG))) {
                            if (!StringUtil.hasText(broadcastBookingVehicleItemIds) || (broadcastBookingVehicleItemIds.indexOf(bookingVehicleItemId)==-1)) {
                                ObmBookingVehicleItemDAO.getInstance().deleteByBookingVehicleItemId(bookingVehicleItemId);
                                continue;
                            }
                        } else {
                            if (!driverUserId.equals(map.get(ObmBookingVehicleItemDAO.COLUMN_DRIVER_LOGIN_USER_ID)) &&
                                    !driverUserId.equals(map.get(ObmBookingVehicleItemDAO.COLUMN_ASSIGN_DRIVER_USER_ID))) {
                                ObmBookingVehicleItemDAO.getInstance().deleteByBookingVehicleItemId(bookingVehicleItemId);
                                continue;
                            }
                        }
                        itemSize++;
                        if (itemSize>400) {
                            ObmBookingVehicleItemDAO.getInstance().deleteByBookingVehicleItemId(bookingVehicleItemId);
                        }
                    }
                }
			}
		}
		return resultMap;
	}

    /**
     * <p>acceptOrRejectBooking</p>
     *
     * @param bookingVehicleItemId
     * @param action
     * @return Const.VALUE_FAIL / Const.VALUE_SUCCESS /Const.VALUE_ACCEPTED
     */
	public static String acceptOrRejectBooking(String bookingVehicleItemId, String action) {
		try {
			String userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
	        if (StringUtil.isNotEmpty(userId)) {
                HttpConnection httpConnection = new HttpConnection(false);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair(ObsConst.KEY_BOOKING_VEHICLE_ITEM_ID, bookingVehicleItemId));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, userId));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_ACTION, action));
                httpConnection.post(ObsConst.URL_RESPONSE_BROADCAST_BOOKING, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                String responseJsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                JSONObject responseJsonObject = new JSONObject(responseJsonData);
                return responseJsonObject.getString(Const.KEY_RESULT);
	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Const.VALUE_FAIL;
	}

    /**
     * <p>Regist device to server</p>
     *
     * @param regId PreferenceUtil.getString(ObsConst.KEY_REG_ID_OBS);
     * @param state ObsConst.VALUE_NO
     * @return ObsConst.VALUE_FAIL ObsConst.VALUE_SUCCESS
     */
    public static String registDevice(String regId, String state) {
        return registDevice(regId, PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS), state);
    }
	/**
	 * <p>Regist device to server</p>
	 * 
	 * @param regId PreferenceUtil.getString(ObsConst.KEY_REG_ID_OBS);
     * @param userId PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS));
	 * @param state ObsConst.VALUE_NO
	 * @return ObsConst.VALUE_FAIL ObsConst.VALUE_SUCCESS 
	 */
	public static String registDevice(String regId, String userId, String state) {
		String result = Const.VALUE_FAIL;
		try {
			HttpConnection httpConnection = new HttpConnection(false);
	        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, userId));
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_PLATFORM, "android"));
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_USE_PUSH_NOTIFICATION, state));
	    	pairs.add(new BasicNameValuePair(ObsConst.KEY_DEVICE_TOKEN, regId));
	    	httpConnection.post(ObsConst.URL_REGISTE_DEVICE, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
	    	String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
	    	JSONObject jsonObject = new JSONObject(jsonData);
	    	result = jsonObject.getString("result");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	return result;
	}
}