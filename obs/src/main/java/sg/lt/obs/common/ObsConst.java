/**
 * Copyright (c) Gan Jianping, 2013. All rights reserved.
 * Author: GanJianping
 */
package sg.lt.obs.common;

public abstract class ObsConst {
	//------------------------------ App info -----------------------------
	public static final String APP_VENDOR = "GANJP";
	public static final String APP_PACKAGE = "sg.lt.obs";
	public static final String APP_VERSION = "1.0.0";
	public static final String APP_FLURRY_KEY = "GBTS23FZ6DXH7KQ27R4G";
	public static final String APP_PROJECT_NUMBER = "1083654704210";
	
	//----------------------------- Obs -----------------
    public static final String DATABASE_NAME = "ltobs.db";
    public static final int DATABASE_VERSION = 1;
//	public static final String SERVER_IP = "http://119.9.74.121";
	public static final String SERVER_IP = "http://192.168.1.5:8080/obs";
	public static final String URL_LOGIN = SERVER_IP + "/allOrg/login";
	public static final String URL_GET_BM_CONFIGS = SERVER_IP + "/mobile/getBmConfigs";
	public static final String URL_GET_DRIVER_BOOKING = SERVER_IP + "/free/driver/booking/";
	public static final String URL_REGISTE_DEVICE = SERVER_IP + "/free/device/regist";
	public static final String URL_ACCEPT_BOOKING = SERVER_IP + "/free/booking/accept";
	
	public static final String KEY_LOGIN_USER_CD_OR_EMAIL_OR_MOBILE_NUMBER = "userCdOrEmailOrMobileNumber";
	public static final String KEY_LOGIN_USER_PASSWORD    = "userPassword";
	public static final String KEY_OBS                    = "OBS";
	public static final String KEY_NETWORK_STATUS_OBS     = "ObsNetworkState";
	public static final String KEY_DB_VERSION_OBS         = "ObsDbVersion";
	public static final String KEY_USER_CD_OBS            = "ObsUserCd";
	public static final String KEY_PASSWORD_OBS           = "ObsPassword";
	public static final String KEY_USER_ID_OBS = "ObsUserId";
	public static final String KEY_USER_NAME_OBS          = "ObsUserName";
	public static final String KEY_USER_MOBILE_PHONE_OBS  = "ObsUserMobilePhone";
	public static final String KEY_USER_EMAIL_OBS         = "ObsUserEmail";
	public static final String KEY_USER_PHOTO_URL_OBS     = "ObsUserPhotoUrl";
	public static final String KEY_USER_EXTEND_ITEMS_OBS  = "ObsUserExtendItems";
	public static final String KEY_ORG_ID_OBSD            = "ObsOrgId";
	public static final String KEY_ORG_NAME_OBSD          = "ObsOrgName";
	public static final String KEY_ORG_MOBILE_NUMBER_OBSD = "ObsOrgMobileNumber";
	public static final String KEY_ORG_EMAIL_OBSD         = "ObsOrgEmail";
	public static final String KEY_ORG_PHONE_NUMBER_OBSD  = "ObsOrgPhoneNumber";
	public static final String KEY_ORG_ADDRESS_OBSD       = "ObsOrgAddress";
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

	public static final String VALUE_BOOKING_SERVICE_CD_ARRIVAL   = "0101";
	public static final String VALUE_BOOKING_SERVICE_CD_DEPARTURE = "0102";
	
	public static final String KEY_PREFERENCE_CONFIG_LAST_TIME = APP_PACKAGE + ".bmConfigLastTime";
	public static final String KEY_PREFERENCE_BOOKING_VEHICLE_UPDATE_ITEM_LAST_TIME = APP_PACKAGE + ".obmBookingVehicleItemUpdateLastTime";
	
	public static final String VALUE_LOGIN_USER_CD = "mobile";
	public static final String VALUE_LOGIN_PASSWORD = "1";

    public static final Boolean IGNORE_SSL = false;
	
	//----------------------------- Knowledge --------------------------
	public static final int PROGRAM_ANDROID = 1;
	public static final int PROGRAM_IOS = 2;
	public static final int NEWS_MOBILE_APP = 11;
}