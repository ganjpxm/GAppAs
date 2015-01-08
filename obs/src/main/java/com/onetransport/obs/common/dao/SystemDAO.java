/**
 * SystemDAO.java
 * 
 * Created by Gan Jianping on 12/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package com.onetransport.obs.common.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>SystemDAO</p>
 * <p>
 *   SystemDAO systemDao = (SystemDAO) (DAOFactory.getInstance().getDAO(DAOType.SYSTEM, getApplication()));
 * </p>
 * 
 * @author Gan Jianping
 *
 */
public class SystemDAO extends DAO {
	protected static final String createSQL = "CREATE TABLE IF NOT EXISTS system (key TEXT PRIMARY KEY, value TEXT)";

	public SystemDAO(Context context, DatabaseHelper dbHelper) {
		super(context, "system", dbHelper);
	}

	/**
	 * <p>Create a system table</p>
	 */
	@Override
	protected void createTable() {
		SQLiteDatabase db = null;
		try { 
			db = this.getDatabase();
			db.execSQL(createSQL);
		} catch( Exception ex ) {
			Log.d("SystemDAO", "createTable:exception:"+ex);
		}
	}	

	/**
	 * <p>Get a boolean value for the key</p>
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBooleanValue(String key, boolean isDefault) {
		String value = getValue(key);
		if (value == null)
			return isDefault;
		if ( value!=null && value.toLowerCase().equals("true") )
			return true;
		return false;
	}
	
	/**
	 * Get an integer value for a key
	 * 
	 * @param key
	 * @return
	 */
	public int getIntegerValue(String key, int defaultValue){
		String value = getValue(key);
		if(value!=null)
			return Integer.parseInt(value);
		return defaultValue;
	}
	/**
	 * <p>Set the boolean value for the key</p>
	 * 
	 * @param key
	 * @param value
	 */
	public void setBooleanValue(String key, boolean value) {
		this.addOrUpdateKey(key, value?"true":"false");
	}
	
	/**
	 * <p>Add or update key</p>
	 * 
	 * @param key
	 * @param value
	 */
	public void addOrUpdateKey(String key, String value ) {
		ContentValues values = new ContentValues();
		values.put("key", key);
		values.put("value", value);
		this.insertOrUpdate(values, "key = ?", new String []{key});
	}
	
	/**
	 * <p>Get the value for a key</p>
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		String query = "SELECT value FROM system WHERE key = ?";
		
		SQLiteDatabase db = null;
		String value = null;
		Cursor cursor = null;
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(query, new String[] {key});
			if( cursor == null ) return null;
			
			if( cursor.getCount() > 0 ) {
				cursor.moveToFirst();
				value = cursor.getString(0);	
			}
		} catch( Exception ex ) {
			Log.d("SystemDAO", "getValue:exception:"+ex);
		}
		finally {
			if( cursor != null ) {
				cursor.deactivate();
				cursor.close();
			}
		}
		return value;
	}
	
}
