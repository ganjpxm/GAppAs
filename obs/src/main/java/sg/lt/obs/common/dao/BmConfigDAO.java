/**
 * BmConfigDAO.java
 * 
 * Created by Gan Jianping on 20/07/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.dao;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.ganjp.glib.core.Const;
import org.ganjp.glib.core.util.StringUtil;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.dao.ObsDaoFactory.DAOType;
import sg.lt.obs.common.entity.BmConfig;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.PreferenceUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>Config table DAO</p>
 * <pre>
 * BmConfigDAO bmConfigDAO = (BmConfigDAO) (DAOFactory.getInstance().getDAO(DAOType.LOANPRODUCT, getApplication()));
 * </pre>
 * 
 * @author Gan Jianping
 *
 */
public class BmConfigDAO extends DAO {
	private static final String TAG = "BmConfigDAO"; 
	private static final String TABLE_NAME = "bm_config"; 
	
	
	// Config Table Columns names
	public static final String COLUMN_CONFIG_ID = "config_id";
	public static final String COLUMN_CONFIG_CD = "config_cd";
	public static final String COLUMN_CONFIG_NAME = "config_name";
	public static final String COLUMN_CONFIG_VALUE = "config_value";
	public static final String COLUMN_DESCRIPTION = "description";
	
	//Field name : NULL, INTEGER, REAL, TEXT, BLOB
	protected static final String createSQL = new StringBuffer().append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (")
			.append(COLUMN_CONFIG_ID).append(" TEXT primary key, ")
			.append(COLUMN_CONFIG_CD).append(" TEXT, ")
			.append(COLUMN_CONFIG_NAME).append(" TEXT, ")
			.append(COLUMN_CONFIG_VALUE).append(" TEXT, ")
			.append(COLUMN_DESCRIPTION).append(" TEXT, ")
			.append(Const.COLUMN_LANG).append(" TEXT, ")
			.append(Const.COLUMN_CREATE_TIME).append(" INTEGER, ")
			.append(Const.COLUMN_MODIFY_TIMESTAMP).append(" INTEGER, ")
			.append(Const.COLUMN_DATA_STATE).append(" TEXT)")
			.toString();

	/**
	 * <p>Config DAO</p>
	 * 
	 * @param context
	 * @param dbHelper
	 */
	public BmConfigDAO(Context context, DatabaseHelper dbHelper) {
		super(context, TABLE_NAME, dbHelper);
	}

	/**
	 * <p>Create bm_config Table</p>
	 */
	@Override
	protected void createTable() {
		SQLiteDatabase db = null;
		try { 
			db = this.getDatabase();
			db.execSQL(createSQL);
		} catch( Exception ex ) {
			Log.d("BmConfigDAO", "createTable:exception:"+ex);
		} finally {
			db.close();
		}
	}	
	
	/**
	 * <p>Insert Or Update a record of Config</p>
	 * 
	 * @param bmConfigs
	 * @return
	 */
	public long insertOrUpdate(BmConfig[] bmConfigs) {
		long latestTime = PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_CONFIG_LAST_TIME);
		for (BmConfig bmConfig : bmConfigs) {
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_CONFIG_ID, bmConfig.getConfigId());
			cv.put(COLUMN_CONFIG_CD, bmConfig.getConfigCd());
			cv.put(COLUMN_CONFIG_NAME, bmConfig.getConfigName());
			cv.put(COLUMN_CONFIG_VALUE, bmConfig.getConfigValue());
			cv.put(COLUMN_DESCRIPTION, bmConfig.getDescription());
			cv.put(Const.COLUMN_CREATE_TIME, bmConfig.getCreateDateTime().getTime());
			cv.put(Const.COLUMN_MODIFY_TIMESTAMP, bmConfig.getModifyTimestamp().getTime());
			cv.put(Const.COLUMN_DATA_STATE, bmConfig.getDataState());
			super.insertOrUpdateWithTime(cv, COLUMN_CONFIG_ID + " = ?", new String[]{bmConfig.getConfigId()});
			if (latestTime<bmConfig.getModifyTimestamp().getTime()) {
				latestTime = bmConfig.getModifyTimestamp().getTime();
			}
		}
		return latestTime;
	}
	
	/**
	 * <p>Insert Or Update a record of Config</p>
	 * 
	 * @param values
	 * @param configId
	 * @return
	 */
	public boolean insertOrUpdate(ContentValues values, String configId) {
		return super.insertOrUpdateWithTime(values, COLUMN_CONFIG_ID + " = ?", new String[]{configId});
	}

	/**
	 * <p>Get all the bmConfig data</p>
	 * 
	 * @return List<BmConfig>
	 */
	public List<BmConfig> getBmConfigs(String lang) {
		List<BmConfig> bmConfigs = new LinkedList<BmConfig>();
		
		String query = "SELECT * FROM " + TABLE_NAME;
		if (StringUtil.isNotEmpty(lang)) {
			query += " where " + Const.COLUMN_LANG + "='" + lang + "'";
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(query, null);
			if( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();

				while(cursor.isAfterLast()==false) {
					BmConfig bmConfig = new BmConfig();
					setBmConfig(bmConfig, cursor);
					
					bmConfigs.add(bmConfig);
					cursor.moveToNext();
				}
			}
		} catch( Exception ex ) {
			Log.e(TAG, ex.getMessage());
		}finally{
			if ( cursor!=null ){
				cursor.close();	
				cursor = null;
			}
			if (db!=null) {
				db.close();
			}
		}
		return bmConfigs;
	}
	
	/**
	 * <p>Get a BmConfig object</p>
	 * 
	 * @param configId
	 * @return BmConfig
	 */
	public BmConfig getBmConfig(String configId) {
		String query = "SELECT * FROM " + TABLE_NAME + " where " + COLUMN_CONFIG_ID + "='" + configId + "'";
		Log.d(TAG, "query:"+query);
		SQLiteDatabase db = null;
		Cursor cursor = null;
		BmConfig bmConfig = new BmConfig();
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(query, null);
			if (cursor!=null && cursor.getCount()>0) {
				cursor.moveToFirst();
				setBmConfig(bmConfig, cursor);
			}
		} catch( Exception ex ) {
		}finally{
			if ( cursor!=null ){
				cursor.close();	
				cursor = null;
			}
			if (db!=null) {
				db.close();
			}
		}
		return bmConfig;
	}
	
	/**
	 * <p>getBmConfig</p>
	 * 
	 * @param configCd
	 * @param lang
	 * @return
	 */
	public BmConfig getBmConfig(String configCd, String lang) {
		String query = "SELECT * FROM " + TABLE_NAME + " where " + COLUMN_CONFIG_CD + "='" + configCd + "' and " + Const.COLUMN_LANG + "='" + lang + "'";
		Log.d(TAG, "query:"+query);
		SQLiteDatabase db = null;
		Cursor cursor = null;
		BmConfig bmConfig = new BmConfig();
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(query, null);
			if (cursor!=null && cursor.getCount()>0) {
				cursor.moveToFirst();
				setBmConfig(bmConfig, cursor);
			}
		} catch( Exception ex ) {
		}finally{
			if ( cursor!=null ){
				cursor.close();	
				cursor = null;
			}
			if (db!=null) {
				db.close();
			}
		}
		return bmConfig;
	}
	/**
	 * <p>Delete table data by configId</p>
	 * 
	 * @param configId
	 * @return
	 */
	public boolean deleteByProductId(String configId) {
		return super.delete(COLUMN_CONFIG_ID + " = ?", new String[]{configId});
	}
	
	/**
	 * <p>Set bmConfig record data to BmConfig object</p>
	 * 
	 * @param bmConfig
	 * @param cursor
	 */
	private void setBmConfig(BmConfig bmConfig, Cursor cursor) {
		if (bmConfig == null) {
			bmConfig = new BmConfig();
		}
		bmConfig.setConfigId(cursor.getString(cursor.getColumnIndex(COLUMN_CONFIG_ID)));
		bmConfig.setConfigCd(cursor.getString(cursor.getColumnIndex(COLUMN_CONFIG_CD)));
		bmConfig.setConfigName(cursor.getString(cursor.getColumnIndex(COLUMN_CONFIG_NAME)));
		bmConfig.setConfigValue(cursor.getString(cursor.getColumnIndex(COLUMN_CONFIG_VALUE)));
		bmConfig.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
		bmConfig.setCreateDateTime(new Timestamp(cursor.getLong(cursor.getColumnIndex(Const.COLUMN_CREATE_TIME))));
		bmConfig.setModifyTimestamp(new Timestamp(cursor.getLong(cursor.getColumnIndex(Const.COLUMN_MODIFY_TIMESTAMP))));
		bmConfig.setDataState(cursor.getString(cursor.getColumnIndex(Const.COLUMN_DATA_STATE)));
	}
	
	/**
	 * <p>Insert or update column's value base on config id</p>
	 * 
	 * @param configId
	 * @param collumnName
	 * @param value
	 */
	public static void insertOrUpdate(String configId, String columnName, String value) {
		BmConfigDAO bmConfigDao = (BmConfigDAO) (ObsDaoFactory.getInstance().getDAO(DAOType.BM_CONFIG, ObsApplication.getAppContext()));
		ContentValues cv = new ContentValues();
		cv.put(columnName, value);
		bmConfigDao.insertOrUpdate(cv, configId);
	}
	
	/**
	 * <p>Get BmConfig DAO</p>
	 */
	public static BmConfigDAO getInstance() {
		return (BmConfigDAO) (ObsDaoFactory.getInstance().getDAO(DAOType.BM_CONFIG, ObsApplication.getAppContext()));
	}
	
}
