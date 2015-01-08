package com.onetransport.obs.common.gcm;

import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.SystemUtil;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.other.PreferenceUtil;

import android.content.Context;
import android.util.Log;

public class GcmUtil {
	
	static final String TAG = "GcmUtil";
    
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
    public static String getRegistrationId(Context context) {
        String registrationId = PreferenceUtil.getString(ObsConst.KEY_REG_ID_OBSD);
        if (StringUtil.isEmpty(registrationId)) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID since the existing regID is not guaranteed to work with the new app version.
        int registeredVersion = PreferenceUtil.getInt(ObsConst.KEY_APP_VERSION_OBSD);
        int currentVersion = SystemUtil.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    public static void storeRegistrationId(Context context, String regId) {
        int appVersion = SystemUtil.getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        PreferenceUtil.saveString(ObsConst.KEY_REG_ID_OBSD, regId);
        PreferenceUtil.saveInt(ObsConst.KEY_APP_VERSION_OBSD, appVersion);
    }

}
