/**
 * SystemUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;

/**
 * <p>System Utility</p>
 *
 * @author GanJianping
 * @since 1.0
 */
public class SystemUtil {
    static Typeface typefaceFrutigerBold;
    static Typeface typefaceFrutigerRoman;

	/**
	 * <p>has sd card</p>
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * get the type face we used in app
     *
     * @param ctx
     *            the context
     * @return the typeface we need
     */
    public static final Typeface getFrutigerBold(Context ctx) {
        if (typefaceFrutigerBold == null) {
            typefaceFrutigerBold = Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/FrutigerLTStd-Bold.ttf");
        }
        return typefaceFrutigerBold;
    }

    /**
     * get the type face we used in app
     *
     * @param ctx
     *            the context
     * @return the typeface we need
     */
    public static final Typeface getFrutigerRoman(Context ctx) {
        if (typefaceFrutigerRoman == null) {
            typefaceFrutigerRoman = Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/FrutigerLTStd-Roman.ttf");
        }
        return typefaceFrutigerRoman;
    }
}
