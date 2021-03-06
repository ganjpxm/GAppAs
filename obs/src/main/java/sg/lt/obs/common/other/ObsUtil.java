/**
 * Copyright (c) Gan Jianping, 2013. All rights reserved.
 * Author: GanJianping
 */
package sg.lt.obs.common.other;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.codehaus.jackson.map.ObjectMapper;
import org.ganjp.glib.core.entity.Response;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.FileUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.MultipartUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.base.Const;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;

import org.ganjp.glib.core.util.SystemUtil;
import org.json.JSONObject;

public abstract class ObsUtil {

    public static Location sLastLocation;
    public static String sLastAddress;
    public static String sLastDateTime;
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
        if (url.indexOf("?")!=-1) {
            url += "&version=1.2";
        } else {
            url += "?version=1.2";
        }
		pHttpConnection.get(url);
		if (pHttpConnection.getResponse()!=null) {
			String jsonData = HttpConnection.processEntity(pHttpConnection.getResponse().getEntity());
			JSONObject jsonObject = new JSONObject(jsonData);
			String data = jsonObject.getString("data");
            String broadcastBookingVehicleItemIds = jsonObject.getString("broadcastBookingVehicleItemIds");
            resultMap.put("broadcastBookingVehicleItemIds", broadcastBookingVehicleItemIds);
            PreferenceUtil.saveString(ObsConst.KEY_BATCH_BROADCAST_BOOKING_VEHICLE_ITEM_IDS, jsonObject.getString("batchBroadcastBookingVehicleItemIds"));
			if (StringUtil.hasText(data) && !"[]".equalsIgnoreCase(data)) {
		    	ObjectMapper mapper = new ObjectMapper();
		    	obmBookingVehicleItems = mapper.readValue(data, ObmBookingVehicleItem[].class);
                for (ObmBookingVehicleItem obmBookingVehicleItem : obmBookingVehicleItems) {
                    if (StringUtil.hasText(obmBookingVehicleItem.getLeadPassengerSignaturePath())) {
                        String signatureFullPath = ObsUtil.getSignatureFullPath(obmBookingVehicleItem.getLeadPassengerSignaturePath());
                        downloadFromUrl(ObsConst.SERVER_IP + obmBookingVehicleItem.getLeadPassengerSignaturePath(), signatureFullPath);
                    }
                }
                resultMap.put("updateSize", String.valueOf(obmBookingVehicleItems.length));
                if (pIsUpdate==false) {
                    ObmBookingVehicleItemDAO.getInstance().dropTable();
                    ObmBookingVehicleItemDAO.getInstance().createTable();
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

        try {
            String signaturePathBookingVehicleItemIds = PreferenceUtil.getString(ObsConst.KEY_SIGNATURE_PATH_BOOKING_VEHICLE_ITEM_IDS);
            if (StringUtil.hasText(signaturePathBookingVehicleItemIds) && signaturePathBookingVehicleItemIds.indexOf(",")!=-1) {
                String[] signaturePathBookingVehicleItemIdArr = signaturePathBookingVehicleItemIds.split(";");
                for (String signaturePathBookingVehicleItemId : signaturePathBookingVehicleItemIdArr) {
                    final String[] arr = signaturePathBookingVehicleItemId.split(",");
                    new ObsUtil.UploadSignatureTask(ObsApplication.getAppContext()).execute(ObsConst.URL_UPLOAD_SIGNATURE, arr[0], arr[1]);
                }
            }
        } catch (Exception ex) {

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
            pairs.add(new BasicNameValuePair(ObsConst.KEY_OS_VERSION, Build.VERSION.RELEASE));
            pairs.add(new BasicNameValuePair(ObsConst.KEY_DEVICE_BRAND, Build.BRAND));
            pairs.add(new BasicNameValuePair(ObsConst.KEY_DEVICE_MODEL, Build.MODEL));

	    	httpConnection.post(ObsConst.URL_REGISTE_DEVICE, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
	    	String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
	    	JSONObject jsonObject = new JSONObject(jsonData);
	    	result = jsonObject.getString("result");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	return result;
	}

    public static String driverOk(String bookingVehicleItemId) {
        String result = Const.VALUE_FAIL;
        try {
            String userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
            if (StringUtil.isNotEmpty(userId)) {
                HttpConnection httpConnection = new HttpConnection(false);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, userId));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_NAME, PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS)));

                httpConnection.post(ObsConst.SERVER_IP + "/web/01/" + bookingVehicleItemId + "/driverOk", new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                JSONObject jsonObject = new JSONObject(jsonData);
                result = jsonObject.getString("result");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String changeDriverClaim(String bookingVehicleItemId, String driverClaimCurrency, String driverClaimPrice) {
        String result = Const.VALUE_FAIL;
        try {
            String userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
            if (StringUtil.isNotEmpty(userId)) {
                HttpConnection httpConnection = new HttpConnection(false);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, userId));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_NAME, PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS)));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_BOOKING_VEHICLE_ITEM_ID, bookingVehicleItemId));
                pairs.add(new BasicNameValuePair("driverClaimCurrency", driverClaimCurrency));
                pairs.add(new BasicNameValuePair("driverClaimPrice", driverClaimPrice));
                pairs.add(new BasicNameValuePair("deviceName", SystemUtil.getDeviceName()));

                httpConnection.post(ObsConst.SERVER_IP + "/web/01/changeDriverClaim", new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                JSONObject jsonObject = new JSONObject(jsonData);
                result = jsonObject.getString("result");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String trackLocation() {
        String result = Const.VALUE_FAIL;
        try {
            String lastDateTime = ObsUtil.sLastDateTime;
            if (StringUtil.hasText(lastDateTime) && lastDateTime.indexOf(" ")!=-1) {
                String[] arr = lastDateTime.split(" ");
                String trackDate = arr[0];
                String address = ObsUtil.sLastAddress.replaceAll("_", "-").replaceAll(";", ".");
                String trackContent = arr[1] + "_" + ObsUtil.sLastLocation.getLatitude() + "_" + ObsUtil.sLastLocation.getLongitude() + "_" + address + ";";
                HttpConnection httpConnection = new HttpConnection(false);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS)));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_NAME, PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS)));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_TRACK_DATE, trackDate));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_TRACK_CONTENT, trackContent));
                httpConnection.post(ObsConst.URL_TRACK_LOCATION, new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                JSONObject jsonObject = new JSONObject(jsonData);
                result = jsonObject.getString("result");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String getRelateDriverInfos(String vehicleTypeCd) {
        String result = Const.VALUE_FAIL;
        try {
            String userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
            if (StringUtil.isNotEmpty(userId)) {
                HttpConnection httpConnection = new HttpConnection(false);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, userId));
                pairs.add(new BasicNameValuePair("vehicleTypeCd", vehicleTypeCd));

                httpConnection.post(ObsConst.SERVER_IP + "/web/01/getRelateDriverInfos", new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                JSONObject jsonObject = new JSONObject(jsonData);
                result = jsonObject.getString("result");
                if (Const.VALUE_SUCCESS.equalsIgnoreCase(result)) {
                    return jsonObject.getString("data");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String reassignDriver(String bookingVehicleItemId, String vehicleId, String driverUserName, String driverMobileNumber,
                                        String driverVehicle, String driverLoginUserId, String assignDriverUserId, String assignDriverUserName,
                                        String assignDriverMobilePhone, String isPast) {
        String result = Const.VALUE_FAIL;
        try {
            String userId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
            if (StringUtil.isNotEmpty(userId)) {
                HttpConnection httpConnection = new HttpConnection(false);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_ID, userId));
                pairs.add(new BasicNameValuePair(ObsConst.KEY_USER_NAME, PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS)));
                pairs.add(new BasicNameValuePair("driverUserId", vehicleId));
                pairs.add(new BasicNameValuePair("bookingVehicleItemId", bookingVehicleItemId));
                pairs.add(new BasicNameValuePair("driverUserName", driverUserName));
                pairs.add(new BasicNameValuePair("deviceName", SystemUtil.getDeviceName()));
                pairs.add(new BasicNameValuePair("isDriverAccept", "no"));
                pairs.add(new BasicNameValuePair("past", isPast));
                pairs.add(new BasicNameValuePair("driverMobileNumber", driverMobileNumber));
                pairs.add(new BasicNameValuePair("driverVehicle", driverVehicle));
                pairs.add(new BasicNameValuePair("driverLoginUserId", driverLoginUserId));
                pairs.add(new BasicNameValuePair("assignDriverUserId", assignDriverUserId));
                pairs.add(new BasicNameValuePair("assignDriverUserName", assignDriverUserName));
                pairs.add(new BasicNameValuePair("assignDriverMobilePhone", assignDriverMobilePhone));

                httpConnection.post(ObsConst.SERVER_IP + "/web/01/assignDriver", new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                String jsonData = HttpConnection.processEntity(httpConnection.getResponse().getEntity());
                JSONObject jsonObject = new JSONObject(jsonData);
                result = jsonObject.getString("result");
                if (Const.VALUE_SUCCESS.equalsIgnoreCase(result)) {
                    return jsonObject.getString("data");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String getSignatureName(String leadPassengerSignaturePath) {
        if (StringUtil.hasText(leadPassengerSignaturePath) && leadPassengerSignaturePath.indexOf("/")!=-1) {
            return leadPassengerSignaturePath.substring(leadPassengerSignaturePath.lastIndexOf("/") + 1, leadPassengerSignaturePath.length());
        } else {
            return "";
        }
    }

    public static String getSignatureFullPath(String leadPassengerSignaturePath) {
        String signatureName = getSignatureName(leadPassengerSignaturePath);
        if (StringUtil.hasText(signatureName)) {
            return ObsUtil.getSignatureDirPath() + signatureName;
        } else {
            return "";
        }
    }

    public static String getSignatureDirPath() {
        File file;
        file = ObsApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file!=null && !file.exists()) {
            file.mkdirs();
        }
        String dirPath = file.getAbsolutePath() + File.separatorChar + ObsConst.KEY_SIGNATURE;
        if (!new File(dirPath).exists()) {
            new File(dirPath).mkdir();
        }
        return dirPath + File.separatorChar;
    }
    /**
     * <p>submit file and application No to server</p>
     *
     * @param requestURL
     * @param fileFullPath
     * @param bookingVehicleItemId
     * @return
     */
    public static Response uploadSignature(String requestURL, String fileFullPath, String bookingVehicleItemId) {
        try {
            MultipartUtil multipart = new MultipartUtil(requestURL, "UTF-8", false);
            multipart.addFilePart("files", new File(fileFullPath));
            multipart.addFormField("bookingVehicleItemId", bookingVehicleItemId + "," + PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS) + "," +  SystemUtil.getDeviceName());
            return multipart.getResponse();
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static void downloadFromUrl(final String imageURL, final String fileFullPath) {
        if (!new File(fileFullPath).exists()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        URL url = new URL(imageURL);
                        File file = new File(fileFullPath);

                        URLConnection urlConnection = url.openConnection();
                        InputStream inpuntStream = urlConnection.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(inpuntStream);
                        ByteArrayBuffer baf = new ByteArrayBuffer(50);
                        int current = 0;
                        while ((current = bis.read()) != -1) {
                            baf.append((byte) current);
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(baf.toByteArray());
                        fos.close();
                    } catch (IOException e) {
                        Log.d("ObsUtil", "Error: " + e);
                    }
                }
            }).start();
        }
    }

    /**
     * <p>Upload signature</p>
     */
    public static class UploadSignatureTask extends AsyncTask<String, Integer, Response> {
        Context context = null;
        public UploadSignatureTask(Context ctx) {
            super();
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            //TODO
        }

        @Override
        protected Response doInBackground(String... param) {
            Response response = null;
            try {
                response = ObsUtil.uploadSignature(param[0], param[1], param[2]);
                response.setMessage(param[1] + "," + param[2]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            try {
                if (response!=null) {
                    String result = response.getResult();
                    if (Response.STATUS_SUCCESS.equalsIgnoreCase(result)) {
                        String signaturePathBookingVehicleItemIds = PreferenceUtil.getString(ObsConst.KEY_SIGNATURE_PATH_BOOKING_VEHICLE_ITEM_IDS);
                        if (StringUtil.hasText(signaturePathBookingVehicleItemIds)) {
                            if (signaturePathBookingVehicleItemIds.indexOf(";")!=-1) {
                                if (signaturePathBookingVehicleItemIds.indexOf(signaturePathBookingVehicleItemIds + ";")!=-1) {
                                    signaturePathBookingVehicleItemIds = signaturePathBookingVehicleItemIds.replaceAll(signaturePathBookingVehicleItemIds + ";", "");
                                } else {
                                    signaturePathBookingVehicleItemIds = signaturePathBookingVehicleItemIds.replaceAll(signaturePathBookingVehicleItemIds, "");
                                }
                                PreferenceUtil.saveString(ObsConst.KEY_SIGNATURE_PATH_BOOKING_VEHICLE_ITEM_IDS, signaturePathBookingVehicleItemIds);
                            } else {
                                PreferenceUtil.saveString(ObsConst.KEY_SIGNATURE_PATH_BOOKING_VEHICLE_ITEM_IDS, "");
                            }
                        }

                        ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}