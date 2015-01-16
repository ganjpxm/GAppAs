/**
 * Const.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 Gan Jianping. All rights reserved.
 */
package org.ganjp.glib.core;

/**
 * <p>Global Constant</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class Const {
	//------------------------------ Time -----------------------------
	public static final int DURATION_SPLASH = 2 * 1000;  // 2 seconds
	public static final int TIMEOUT_CONNECT = 30 * 1000; // 30 seconds
	public static final int TIMEOUT_SUBMIT_FORM =  20 * 1000; //20 seconds
	public static final int TIMEOUT_UPLOAD = 2 * 60 * 60000; //2 minitues
	public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	
	//------------------------------ File -----------------------------
	public static final String IMG_PREFIX = "IMG_";
	public static final String IMG_SUFFIX = ".jpg";
    public static final String PICTURE_ALBUM = "Pictures";
	
	//----------------------------- Database --------------------------
	public static final String COLUMN_LANG = "lang";
	public static final String COLUMN_CREATE_TIME = "create_date_time";
	public static final String COLUMN_MODIFY_TIMESTAMP = "modify_timestamp";
	public static final String COLUMN_DATA_STATE = "data_state";
	
	//----------------------------- Key and value ------------------
	public static final String KEY_RESULT                 = "result";
    public static final String VALUE_SUCCESS = "success";
    public static final String VALUE_FAIL                 = "fail";
    public static final String VALUE_NEW_ITEMS            = "New Items";
    public static final String VALUE_YES                  = "YES";
    public static final String VALUE_NO                   = "NO";
    public static final String VALUE_ACCEPTED             = "accepted";
    public static final String VALUE_TIMEOUT              = "timeout";

	//---------------------------------------- Network ----------------------------
	public static final String GOOGLE_DOC_VIEW_URL = "https://docs.google.com/gview?embedded=true&url=";
}
