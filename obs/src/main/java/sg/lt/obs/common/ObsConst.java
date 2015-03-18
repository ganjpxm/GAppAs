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
	public static final String APP_PROJECT_NUMBER = "386562612367";
	
	//----------------------------- Obs -----------------
    public static final String DATABASE_NAME = "ltobs.db";
    public static final int DATABASE_VERSION = 1;
	public static final String SERVER_IP = "http://119.9.74.121";
//	public static final String SERVER_IP = "http://192.168.1.4:8080/obs";
	public static final String URL_LOGIN = SERVER_IP + "/allOrg/login";
	public static final String URL_GET_BM_CONFIGS = SERVER_IP + "/mobile/getBmConfigs";
	public static final String URL_GET_DRIVER_BOOKING = SERVER_IP + "/free/driver/booking/";
	public static final String URL_REGISTE_DEVICE = SERVER_IP + "/free/device/regist";
	public static final String URL_RESPONSE_BROADCAST_BOOKING = SERVER_IP + "/web/responseBroadcastBooking";
    public static final String URL_GET_DRIVER_PROFILE = SERVER_IP + "/free/01/driver/profile?isApp=yes&driverUserId=";
    public static final String URL_TRACK_LOCATION = SERVER_IP + "/web/01/track/location";


    public static final String BOOKING_STATUS_CD_PENDING = "0800";
    public static final String BOOKING_STATUS_CD_PROCESSING = "0801";
    public static final String BOOKING_STATUS_CD_CONFIRMED = "0802";
    public static final String BOOKING_STATUS_CD_ASSIGNED = "0803";
    public static final String BOOKING_STATUS_CD_DRIVER_INFO_SENT = "0804";
    public static final String BOOKING_STATUS_CD_UPDATE = "0805";
    public static final String BOOKING_STATUS_CD_REFUND = "0807";
    public static final String BOOKING_STATUS_CD_UNSUCCESSFUL = "0808";
    public static final String BOOKING_STATUS_CD_CANCELLED = "0809";
    public static final String BOOKING_STATUS_CD_MISSED = "0810";
    public static final String BOOKING_STATUS_CD_COMPLETED = "0811";
    public static final String BOOKING_STATUS_CD_ENQUIRY = "0821";

    public static final String ACTION_ACCEPT_ENQUIRY_BOOKING = "AcceptEnquiryBooking";
    public static final String ACTION_REJECT_ENQUIRY_BOOKING = "RejectEnquiryBooking";
    public static final String ACTION_ACCEPT_NEW_BOOKING = "AcceptNewBooking";
    public static final String ACTION_REJECT_NEW_BOOKING = "RejectNewBooking";
    public static final String ACTION_ACCEPT_UPDATE_BOOKING = "AcceptUpdateBooking";
    public static final String ACTION_REJECT_UPDATE_BOOKING = "RejectUpdateBooking";
    public static final String ACTION_ACCEPT_CANCEL_BOOKING = "AcceptCancelBooking";
    public static final String ACTION_REJECT_CANCEL_BOOKING = "RejectCancelBooking";

    public static final String DRIVER_ACTION_VIEWED = "Viewed";
    public static final String DRIVER_ACTION_OK = "OK";

	public static final String KEY_LOGIN_USER_CD_OR_EMAIL_OR_MOBILE_NUMBER = "userCdOrEmailOrMobileNumber";
	public static final String KEY_LOGIN_USER_PASSWORD    = "userPassword";
	public static final String KEY_OBS                    = "OBS";
	public static final String KEY_NETWORK_STATUS_OBS     = "ObsNetworkState";
	public static final String KEY_DB_VERSION_OBS         = "ObsDbVersion";
    public static final String KEY_USER_ID_OBS            = "ObsUserId";
	public static final String KEY_USER_CD_OBS            = "ObsUserCd";
    public static final String KEY_USER_NAME_OBS          = "ObsUserName";
	public static final String KEY_USER_PASSWORD_OBS      = "ObsUserPassword";
	public static final String KEY_USER_MOBILE_NUMBER_OBS = "ObsUserMobileNumber";
	public static final String KEY_USER_EMAIL_OBS         = "ObsUserEmail";
	public static final String KEY_BOOKING_VEHICLE_ITEM_OBS = "ObsBookingVehicleItem";
    public static final String KEY_BROADCAST_BOOKING_VEHICLE_ITEM_IDS = "broadcastBookingVehicleItemIds";
    public static final String KEY_DRIVER_INFO_OBS        = "ObsDriverInfos";
    public static final String KEY_IS_PAST                = "isPast";

	public static final String KEY_REG_ID_OBS             = "ObsRegistrationId";
	public static final String KEY_APP_VERSION_OBS        = "ObsAppVersion";

	public static final String KEY_RESULT                 = "result";
	public static final String KEY_DATA                   = "data";
	public static final String KEY_START_DATE             = "startDate";
	public static final String KEY_USER_ID                = "userId";
    public static final String KEY_USER_NAME              = "userName";
	public static final String KEY_DEVICE_TOKEN           = "deviceToken";
    public static final String KEY_OS_VERSION             = "osVersion";
    public static final String KEY_DEVICE_BRAND           = "deviceBrand";
    public static final String KEY_DEVICE_MODEL           = "deviceModel";
	public static final String KEY_USE_PUSH_NOTIFICATION  = "usePushNotification";
	public static final String KEY_PLATFORM          	  = "platform";
    public static final String KEY_BOOKING_VEHICLE_ITEM_ID = "bookingVehicleItemId";
    public static final String KEY_ACTION                 = "action";
    public static final String KEY_DETAIL_NEEED_REFRESH   = "detailNeedRefresh";
    public static final String KEY_UPCOMING_LIST_NEEED_REFRESH   = "upcomingListNeedRefresh";
    public static final String KEY_HISTORY_LIST_NEEED_REFRESH   = "historyListNeedRefresh";

    public static final String KEY_TRACK_DATE             = "trackDate";
    public static final String KEY_TRACK_CONTENT          = "trackContent";

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

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 30;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 30;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Stores the lat / long pairs in a text file
    public static final String LOCATION_FILE = "sdcard/location.txt";
    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";

}