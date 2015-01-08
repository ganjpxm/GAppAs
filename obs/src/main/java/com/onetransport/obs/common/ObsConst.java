/**
 * Copyright (c) Gan Jianping, 2013. All rights reserved.
 * Author: GanJianping
 */
package com.onetransport.obs.common;

public abstract class ObsConst {
	//------------------------------ App info -----------------------------
	public static final String APP_VENDOR = "GANJP";
	public static final String APP_PACKAGE = "com.onetransport.obs";
	public static final String APP_VERSION = "1.0.0";
	public static final String APP_FLURRY_KEY = "GBTS23FZ6DXH7KQ27R4G";
	public static final String APP_PROJECT_NUMBER = "1083654704210";
	
	//----------------------------- Obs -----------------
    public static final String DATABASE_NAME = "obs.db";
    public static final int DATABASE_VERSION = 1;
	public static final String SERVER_IP = "http://119.9.74.121";
//	public static final String SERVER_IP = "http://192.168.1.41:8080/obs";
	public static final String URL_LOGIN = SERVER_IP + "/allOrg/driver/login";
	public static final String URL_GET_BM_CONFIGS = SERVER_IP + "/mobile/getBmConfigs";
	public static final String URL_GET_DRIVER_BOOKING = SERVER_IP + "/free/driver/booking/";
	public static final String URL_REGISTE_DEVICE = SERVER_IP + "/free/device/regist";
	public static final String URL_ACCEPT_BOOKING = SERVER_IP + "/free/booking/accept";
	
	public static final String KEY_LOGIN_USER_CD_OR_EMAIL = "userCdOrEmail";
	public static final String KEY_LOGIN_USER_PASSWORD    = "userPassword";
	public static final String KEY_OBSD                   = "OBSD";
	public static final String KEY_NETWORK_STATUS_OBSD    = "ObsNetworkState";
	public static final String KEY_DB_VERSION_OBSD        = "OBSCDbVersion";
	public static final String KEY_USER_CD_OBSD           = "ObsdUserCd";
	public static final String KEY_PASSWORD_OBSD          = "ObsdPassword";
	public static final String KEY_USER_ID_OBSD           = "ObsdUserId";
	public static final String KEY_USER_NAME_OBSD         = "ObsdUserName";
	public static final String KEY_USER_MOBILE_PHONE_OBSD = "ObsdUserMobilePhone";
	public static final String KEY_USER_EMAIL_OBSD        = "ObsdUserEmail";
	public static final String KEY_USER_PHOTO_URL_OBSD    = "ObsdUserPhotoUrl";
	public static final String KEY_USER_EXTEND_ITEMS_OBSD = "ObsdUserExtendItems";
	public static final String KEY_ORG_ID_OBSD            = "ObsdOrgId";
	public static final String KEY_ORG_NAME_OBSD          = "ObsdOrgName";
	public static final String KEY_ORG_MOBILE_NUMBER_OBSD = "ObsdOrgMobileNumber";
	public static final String KEY_ORG_EMAIL_OBSD         = "ObsdOrgEmail";
	public static final String KEY_ORG_PHONE_NUMBER_OBSD  = "ObsdOrgPhoneNumber";
	public static final String KEY_ORG_ADDRESS_OBSD       = "ObsdOrgAddress";
	public static final String KEY_BOOKING_VEHICLE_ITEM_ID_OBSD = "ObsdBookingVehicleItemId";
	public static final String KEY_BOOKING_VEHICLE_ITEM_OBSD = "ObsdBookingVehicleItem";

	public static final String KEY_REG_ID_OBSD            = "ObsdRegistrationId";
	public static final String KEY_APP_VERSION_OBSD       = "ObsdAppVersion";

	public static final String KEY_RESULT                 = "result";
	public static final String KEY_DATA                   = "data";
	public static final String KEY_START_DATE             = "startDate";
	public static final String KEY_USER_ID                = "userId";
	public static final String KEY_DEVICE_TOKEN           = "deviceToken";
	public static final String KEY_USE_PUSH_NOTIFICATION  = "usePushNotification";
	public static final String KEY_PLATFORM          	  = "platform";
	

	public static final String VALUE_SCCESS               = "success";
	public static final String VALUE_FAIL                 = "fail";
	public static final String VALUE_NEW_ITEMS            = "New Items";
	public static final String VALUE_YES                  = "YES";
	public static final String VALUE_NO                   = "NO";
	public static final String VALUE_ACCEPTED             = "accepted";
	public static final String VALUE_TIMEOUT              = "timeout";

	public static final String VALUE_BOOKING_SERVICE_CD_ARRIVAL   = "0101";
	public static final String VALUE_BOOKING_SERVICE_CD_DEPARTURE = "0102";
	
	public static final String KEY_PREFERENCE_CONFIG_LAST_TIME = APP_PACKAGE + ".bmConfigLastTime";
	public static final String KEY_PREFERENCE_BOOKING_VEHICLE_ITEM_LAST_TIME = APP_PACKAGE + ".obmBookingVehicleItemLastTime";
	
	public static final String VALUE_LOGIN_USER_CD = "mobile";
	public static final String VALUE_LOGIN_PASSWORD = "1";

    public static final Boolean IGNORE_SSL = false;
	
	//----------------------------- Knowledge --------------------------
	public static final int PROGRAM_ANDROID = 1;
	public static final int PROGRAM_IOS = 2;
	public static final int NEWS_MOBILE_APP = 11;
}