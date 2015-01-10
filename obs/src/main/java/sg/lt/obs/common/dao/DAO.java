/**
 * DAO.java
 * 
 * Created by Gan Jianping on 12/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.dao;

import org.ganjp.glib.core.Const;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>Database Access Object Abstract Class</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public abstract class DAO {
	private static final String TAG = "[core][dao]";
	private static final String TAGClass = "DAO : ";
	
	protected DatabaseHelper mDbHelper = null;
	protected String mTableName;
	protected Context mContext;
	
	protected abstract void createTable(); //Abstract function called to create the table
	
	/**
	 * <p>DAO</p>
	 * 
	 * @param context
	 * @param tablename
	 * @param helper
	 */
	public DAO(Context context, String tablename, DatabaseHelper helper ) {
		this.mContext = context;
		this.mTableName = tablename;
		this.mDbHelper = helper;
		this.initialiseDatabase();
	}
	
	/**
	 * <p>Initialise the database</p>
	 * 
	 */
	protected void initialiseDatabase() {
		if (this.mDbHelper == null) {
			this.mDbHelper = new DatabaseHelper(mContext);
		}
		
		Log.d(TAG, mTableName+":requireUpgrade:"+this.mDbHelper.requireUpgrade());
		if (this.mDbHelper.requireUpgrade()) {
			String query = "DROP TABLE " + this.mTableName + ";";
			SQLiteDatabase db = null;
			try {
				db = this.getDatabase();
				db.execSQL(query);
			} catch( Exception ex ) {
				Log.e(TAG, ex.getMessage());
			}
		}
		this.createTable();
	}
	
	/**
	 * <p>Get the database instance</p>
	 *
	 * @return SQLiteDatabase
	 */
	protected SQLiteDatabase getDatabase() {
		return this.mDbHelper.getDatabase();
	}
	
	/**
	 * <p>Insert Table data</p>
	 * 
	 * @param values
	 * @return
	 */
	public boolean insert(ContentValues values){
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			db.insertOrThrow(this.mTableName, null, values);
			Log.d(TAG, TAGClass + this.mTableName + "insert data Successful");
			return true;
		} catch( Exception ex ) {
			if (db!=null) db.close();
			Log.e(TAG, "insertTable:exception:"+ex);
		}	
		return false;
	}
	
	/**
	 * <p>Insert Table data with time</p>
	 * 
	 * @param values
	 * @return
	 */
	public boolean insertWithTime(ContentValues values){
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			values.put(Const.COLUMN_CREATE_TIME, System.currentTimeMillis());
			values.put(Const.COLUMN_MODIFY_TIMESTAMP, System.currentTimeMillis());
			db.insertOrThrow(this.mTableName, null, values);
			Log.d(TAG, TAGClass + this.mTableName + "insert data Successful");
			return true;
		} catch( Exception ex ) {
			if (db!=null) db.close();
			Log.e(TAG, "insertTable:exception:"+ex);
		}	
		return false;
	}
	
	/**
	 * <p>Insert table data</p>
	 * 
	 * @param values
	 * @param whereClause : productId = ?
	 * @param whereArgs : new String[] {productId}
	 * @return
	 */
	public boolean insertOrUpdate(ContentValues values, String whereClause, String[] whereArgs){
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			if (db.update(mTableName, values, whereClause, whereArgs)<=0)
				db.insertOrThrow(mTableName, null, values);
			Log.d(TAG, TAGClass + this.mTableName + "insert or updata data Successful");
			return true;
		} catch( Exception ex ) {
			Log.e(TAG, "insertTable:exception:"+ex);
		} finally {
			if (db!=null) db.close();
		}
		return false;
	}
	
	/**
	 * <p>Insert table data with time</p>
	 * 
	 * @param values
	 * @param whereClause : productId = ?
	 * @param whereArgs : new String[] {productId}
	 * @return
	 */
	public boolean insertOrUpdateWithTime(ContentValues values, String whereClause, String[] whereArgs){
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			values.put(Const.COLUMN_MODIFY_TIMESTAMP, System.currentTimeMillis());
			if (db.update(mTableName, values, whereClause, whereArgs)<=0) {
				values.put(Const.COLUMN_CREATE_TIME, System.currentTimeMillis());
				db.insertOrThrow(mTableName, null, values);
			}
			Log.d(TAG, TAGClass  + this.mTableName +  " insert Or Update with time Successful");
			return true;
		} catch( Exception ex ) {
			Log.e(TAG, "insertOrUpdateWithTime:exception:"+ex);
		} finally {
			if (db!=null) db.close();
		}
		return false;
	}
	
	/**
	 * <p>Delete table by where</p>
	 * 
	 * @param whereClause : productId = ?
	 * @param whereArgs : new String[] {productId}
	 */
	public boolean delete(String whereClause, String[] whereArgs){
		SQLiteDatabase db = null;
		boolean result = false;
		try {
			db = this.getDatabase();
			db.delete(mTableName, whereClause, whereArgs);
			result = true;
			Log.d(TAG, TAGClass + "delete " + this.mTableName + " Successful");
		} catch( Exception ex ) {
			Log.e(TAG, "insertTable:exception:"+ex);
		} finally {
			if (db!=null) db.close();
		}	
		return result;
	}
	
	/**
	 * <p>Delete table all data</p>
	 */
	public boolean delete() {
		SQLiteDatabase db = null;
		boolean result = false;
		try {
			db = this.getDatabase();
			db.delete(this.mTableName, null, null);
			result = true;
			Log.d(TAG, TAGClass + "delete " + this.mTableName + " all data Successful");
		} catch( Exception ex ) {
			Log.e(TAG, "deleteTable:exception:"+ex);
		} finally {
			if (db!=null) db.close();
		}	
		return result;	
	}	
	
	/**
	 * <p>Get recoder count</p>
	 * 
	 * @return
	 */
	public int getCount() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		int count = 0;
		try {
			db = this.getDatabase();
			cursor = db.rawQuery("SELECT  * FROM " + mTableName, null);
			count = cursor.getCount();
			Log.d(TAG, TAGClass + "get count from " + this.mTableName + " Successful");
		} catch( Exception ex ) {
			Log.e(TAG, "insertTable:exception:"+ex);
		} finally {
			if (db!=null) db.close();
			if (cursor!=null) {
				cursor.close();
				cursor = null;
			}
		}
		return count;
	}
	
	/**
	 * <p>Check if the cursor has rows</p>
	 * 
	 * @param cursor
	 * @return
	 */
	protected boolean cursorHasRows(Cursor cursor) {
		return (cursor!=null && cursor.getCount()>0);
	}
	
	/**
	 * <p>Close the connection</p>
	 */
	protected void close() {
		if (this.mDbHelper != null) {
			this.mDbHelper.close();
		}
	}
}
