package com.onetransport.obs.common.other;

import com.onetransport.obs.common.ObsConst;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * <p>Preference Util</p>
 *
 * @author GanJianping
 * @since 1.0
 */
public class PreferenceUtil {
	/**
	 * <p>saveString</p>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean saveString(String key, String value) {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
        editor.putString(key, value);  
        editor.commit(); 
//        if (StringUtil.isNotEmpty(getString(key))) {
//        	editor.remove(key);
//        }
        return true;
	}
	
	/**
	 * <p>saveLong</p>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean saveLong(String key, long value) {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
        editor.putLong(key, value);  
        editor.commit(); 
        return true;
	}
	
	/**
	 * <p>saveInt</p>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean saveInt(String key, int value) {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
        editor.putInt(key, value);  
        editor.commit(); 
        return true;
	}
	
	/**
	 * <p>getString</p>
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE); 
        return sp.getString(key, "");
	}
	 
	/**
	 * <p>getLong</p>
	 * 
	 * @param key
	 * @return
	 */
	public static long getLong(String key) {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE);  
        return sp.getLong(key, 0);
	} 
	
	/**
	 * <p>getInt</p>
	 * 
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE);  
        return sp.getInt(key, Integer.MIN_VALUE);
	} 
	
	public static boolean clear() {
		SharedPreferences sp = ObsApplication.getAppContext().getSharedPreferences(ObsConst.APP_PACKAGE, Activity.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
        editor.clear();  
        editor.commit(); 
        return true;
	}
}
