/**
 * ObmBookingVehicleItemDAO.java
 * 
 * Created by Gan Jianping on 20/07/13.
 * Copyright (c) 2013 DBS. All rights reserved.
 */
package com.onetransport.obs.common.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.StringUtil;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.dao.ObsDaoFactory.DAOType;
import com.onetransport.obs.common.entity.ObmBookingVehicleItem;
import com.onetransport.obs.common.other.ObsApplication;
import com.onetransport.obs.common.other.PreferenceUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>BookingVehicleItem table DAO</p>
 * <pre>
 * ObmBookingVehicleItemDAO ObmBookingVehicleItemDAO = (ObmBookingVehicleItemDAO) (DAOFactory.getInstance().getDAO(DAOType.LOANPRODUCT, getApplication()));
 * </pre>
 * 
 * @author Gan Jianping
 *
 */
public class ObmBookingVehicleItemDAO extends DAO {
	private static final String TAG = "ObmBookingVehicleItemDAO"; 
	private static final String TABLE_NAME = "obm_booking_vehicle_item";
	public static final String FLAG_UPCOMING = "upcoming";
	public static final String FLAG_PAST = "past";
	
	// BookingVehicleItem Table Columns names
	public static final String  COLUMN_BOOKING_VEHICLE_ITEM_ID      = "bookingVehicleItemId";
	public static final String  COLUMN_BOOKING_VEHICLE_ID           = "bookingVehicleId";
	public static final String  COLUMN_BOOKING_NUMBER               = "bookingNumber";
	public static final String  COLUMN_BOOKING_SERVICE              = "bookingService";
	public static final String  COLUMN_BOOKING_SERVICE_CD           = "bookingServiceCd";
	public static final String  COLUMN_PICKUP_DATE                  = "pickupDate";
	public static final String  COLUMN_PICKUP_TIME                  = "pickupTime";
	public static final String  COLUMN_PICKUP_DATE_TIME             = "pickupDateTime";
	public static final String  COLUMN_BOOKING_GHOURS               = "bookingHours";
	public static final String  COLUMN_FLIGHT_NUMBER                = "flightNumber";
	public static final String  COLUMN_PICKUP_CITY                  = "pickupCity";
	public static final String  COLUMN_PICKUP_CITY_CD               = "pickupCityCd";
	public static final String  COLUMN_PICKUP_ADDRESS               = "pickupAddress";
	public static final String  COLUMN_PICKUP_POINT                 = "pickupPoint";
	public static final String  COLUMN_DESTINATION                  = "destination";
	public static final String  COLUMN_VEHICLE                      = "vehicle";
	public static final String  COLUMN_VEHICLE_CD                   = "vehicleCd";
	public static final String  COLUMN_PRICE                        = "price";
	public static final String  COLUMN_DRIVER_USER_ID               = "driverUserId";
	public static final String  COLUMN_LEAD_PASSENGER_FIRST_NAME    = "leadPassengerFirstName";
	public static final String  COLUMN_LEAD_PASSENGER_LAST_NAME     = "leadPassengerLastName";
	public static final String  COLUMN_LEAD_PASSENGER_MOBILE_NUMBER = "leadPassengerMobileNumber";
	public static final String  COLUMN_BOOKING_STATUS               = "bookingStatus";
	public static final String  COLUMN_BOOKING_STATUS_CD            = "bookingStatusCd";
	public static final String  COLUMN_PAYMENT_STATUS               = "paymentStatus";
	public static final String  COLUMN_PAYMENT_STATUS_CD            = "paymentStatusCd";
	public static final String  COLUMN_DRIVER_USER_NAME            = "driverUserName";
	public static final String  COLUMN_DRIVER_MOBILE_NUMBER        = "driverMobileNumber";
	public static final String  COLUMN_DRIVER_VEHICLE              = "driverVehicle";
	public static final String  COLUMN_LEAD_PASSENGER_GENDER       = "leadPassengerGender";
	public static final String  COLUMN_LEAD_PASSENGER_GENDER_CD    = "leadPassengerGenderCd";
	public static final String  COLUMN_NUMBER_OF_PASSENGER         = "numberOfPassenger";
	public static final String  COLUMN_PAYMENT_MODE                = "paymentMode";
	public static final String  COLUMN_PAYMENT_MODE_CD             = "paymentModeCd";
	public static final String  COLUMN_BOOKING_USER_NAME           = "bookingUserName";
	public static final String  COLUMN_BOOKING_USER_MOBILE_NUMBER  = "bookingUserMobileNumber";
	public static final String  COLUMN_BOOKING_USER_EMAIL          = "bookingUserEmail";
	public static final String  COLUMN_BOOKING_USER_SURNAME        = "bookingUserSurname";
	public static final String  COLUMN_ORG_EMAIL                   = "orgEmail";
	public static final String  COLUMN_ASSIGN_ORG_ID               = "assignOrgId";
	public static final String  COLUMN_ASSIGN_ORG_NAME             = "assignOrgName";
	public static final String  COLUMN_ASSIGN_ORG_EMAIL            = "assignOrgEmail";
	public static final String  COLUMN_ASSIGN_DRIVER_USER_ID       = "assignDriverUserId";
	public static final String  COLUMN_ASSIGN_DRIVER_USER_NAME     = "assignDriverUserName";
	public static final String  COLUMN_ASSIGN_DRIVER_MOBILE_PHONE  = "assignDriverMobilePhone";
	public static final String  COLUMN_ASSIGN_DRIVER_EMAIL         = "assignDriverEmail";
	public static final String  COLUMN_BOOKING_USER_GENDER         = "bookingUserGender";
	public static final String  COLUMN_BOOKING_USER_GENDER_CD      = "bookingUserGenderCd";
	public static final String  COLUMN_AGENT_USER_ID               = "agentUserId";
	public static final String  COLUMN_AGENT_USER_NAME             = "agentUserName";
	public static final String  COLUMN_AGENT_MOBILE_NUMBER         = "agentMobileNumber";
	public static final String  COLUMN_AGENT_EMAIL                 = "agentEmail";
	public static final String  COLUMN_OPERATOR_MOBILE_NUMBER      = "operatorMobileNumber";
	public static final String  COLUMN_OPERATOR_EMAIL              = "operatorEmail";

	public static final String  COLUMN_ORIGIN_URL          = "originUrl";
	public static final String  COLUMN_TITLE               = "title";
	public static final String  COLUMN_REMARK              = "remark";
	public static final String  COLUMN_DESCRIPTION         = "description";
	public static final String  COLUMN_TAG                 = "tag";
	public static final String  COLUMN_DISPLAY_NO          = "displayNo";
	public static final String  COLUMN_ROLE_IDS            = "roleIds";
	public static final String  COLUMN_ORG_ID              = "orgId";
	public static final String  COLUMN_ORG_NAME            = "orgName";
	public static final String  COLUMN_OPERATOR_ID         = "operatorId";
	public static final String  COLUMN_OPERATOR_NAME       = "operatorName";
	public static final String  COLUMN_CREATE_DATE_TIME    = "createDateTime";
	public static final String  COLUMN_MODIFY_TIMESTAMP    = "modifyTimestamp";
	public static final String  COLUMN_DATA_STATE          = "dataState";
	public static final String  COLUMN_SEND_STATE          = "sendState";
	public static final String  COLUMN_SEND_DATE_TIME      = "sendDateTime";
	public static final String  COLUMN_RECIEVE_DATE_TIME   = "recieveDateTime";
	public static final String  COLUMN_QUERY_FILTERS       = "queryFilters";
	
	
	//Field name : NULL, INTEGER, REAL, TEXT, BLOB
	protected static final String createSQL = new StringBuffer().append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (")
			.append(COLUMN_BOOKING_VEHICLE_ITEM_ID).append(" TEXT primary key, ")
			.append(COLUMN_BOOKING_VEHICLE_ID).append(" TEXT, ")
			.append(COLUMN_BOOKING_NUMBER).append(" TEXT, ")
			.append(COLUMN_BOOKING_SERVICE).append(" TEXT, ")
			.append(COLUMN_BOOKING_SERVICE_CD).append(" TEXT, ")
			.append(COLUMN_FLIGHT_NUMBER).append(" TEXT, ")
			.append(COLUMN_PICKUP_CITY).append(" TEXT, ")
			.append(COLUMN_PICKUP_CITY_CD).append(" TEXT, ")
			.append(COLUMN_PICKUP_ADDRESS).append(" TEXT, ")
			.append(COLUMN_PICKUP_POINT).append(" TEXT, ")
			.append(COLUMN_DESTINATION).append(" TEXT, ")
			.append(COLUMN_VEHICLE).append(" TEXT, ")
			.append(COLUMN_VEHICLE_CD).append(" TEXT, ")
			.append(COLUMN_DRIVER_USER_ID).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_FIRST_NAME).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_LAST_NAME).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_MOBILE_NUMBER).append(" TEXT, ")
			.append(COLUMN_BOOKING_STATUS).append(" TEXT, ")
			.append(COLUMN_BOOKING_STATUS_CD).append(" TEXT, ")
			.append(COLUMN_PAYMENT_STATUS).append(" TEXT, ")
			.append(COLUMN_PAYMENT_STATUS_CD).append(" TEXT, ")
			.append(COLUMN_DRIVER_USER_NAME).append(" TEXT, ")
			.append(COLUMN_DRIVER_MOBILE_NUMBER).append(" TEXT, ")
			.append(COLUMN_DRIVER_VEHICLE).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_GENDER).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_GENDER_CD).append(" TEXT, ")
			.append(COLUMN_NUMBER_OF_PASSENGER).append(" TEXT, ")
			.append(COLUMN_PAYMENT_MODE).append(" TEXT, ")
			.append(COLUMN_PAYMENT_MODE_CD).append(" TEXT, ")
			.append(COLUMN_BOOKING_USER_NAME).append(" TEXT, ")
			.append(COLUMN_BOOKING_USER_MOBILE_NUMBER).append(" TEXT, ")
			.append(COLUMN_BOOKING_USER_EMAIL).append(" TEXT, ")
			.append(COLUMN_BOOKING_USER_SURNAME).append(" TEXT, ")
			.append(COLUMN_ORG_EMAIL).append(" TEXT, ")
			.append(COLUMN_ASSIGN_ORG_ID).append(" TEXT, ")
			.append(COLUMN_ASSIGN_ORG_NAME).append(" TEXT, ")
			.append(COLUMN_ASSIGN_ORG_EMAIL).append(" TEXT, ")
			.append(COLUMN_ASSIGN_DRIVER_USER_ID).append(" TEXT, ")
			.append(COLUMN_ASSIGN_DRIVER_USER_NAME).append(" TEXT, ")
			.append(COLUMN_ASSIGN_DRIVER_MOBILE_PHONE).append(" TEXT, ")
			.append(COLUMN_ASSIGN_DRIVER_EMAIL).append(" TEXT, ")
			.append(COLUMN_BOOKING_USER_GENDER).append(" TEXT, ")
			.append(COLUMN_BOOKING_USER_GENDER_CD).append(" TEXT, ")
			.append(COLUMN_AGENT_USER_ID).append(" TEXT, ")
			.append(COLUMN_AGENT_USER_NAME).append(" TEXT, ")
			.append(COLUMN_AGENT_MOBILE_NUMBER).append(" TEXT, ")
			.append(COLUMN_AGENT_EMAIL).append(" TEXT, ")
			.append(COLUMN_OPERATOR_MOBILE_NUMBER).append(" TEXT, ")
			.append(COLUMN_OPERATOR_EMAIL).append(" TEXT, ")
			.append(COLUMN_REMARK).append(" TEXT, ")
			.append(COLUMN_ORG_ID).append(" TEXT, ")
			.append(COLUMN_ORG_NAME).append(" TEXT, ")
			.append(COLUMN_OPERATOR_ID).append(" TEXT, ")
			.append(COLUMN_OPERATOR_NAME).append(" TEXT, ")
			.append(COLUMN_DATA_STATE).append(" TEXT, ")
			.append(COLUMN_SEND_STATE).append(" TEXT, ")
			.append(COLUMN_PRICE).append(" INTEGER, ")
			.append(COLUMN_BOOKING_GHOURS).append(" INTEGER, ")
			.append(COLUMN_DISPLAY_NO).append(" INTEGER, ")
			.append(COLUMN_PICKUP_DATE).append(" INTEGER, ")
			.append(COLUMN_PICKUP_TIME).append(" TEXT, ")
			.append(COLUMN_PICKUP_DATE_TIME).append(" INTEGER, ")
			.append(COLUMN_CREATE_DATE_TIME).append(" INTEGER, ")
			.append(COLUMN_MODIFY_TIMESTAMP).append(" INTEGER, ")
			.append(COLUMN_SEND_DATE_TIME).append(" INTEGER, ")
			.append(COLUMN_RECIEVE_DATE_TIME).append(" INTEGER)")
			.toString();

	/**
	 * <p>BookingVehicleItem DAO</p>
	 * 
	 * @param context
	 * @param dbHelper
	 */
	public ObmBookingVehicleItemDAO(Context context, DatabaseHelper dbHelper) {
		super(context, TABLE_NAME, dbHelper);
	}

	/**
	 * <p>Create obm_booking_vehicle_item Table</p>
	 */
	@Override
	protected void createTable() {
		SQLiteDatabase db = null;
		try { 
			db = this.getDatabase();
			db.execSQL(createSQL);
		} catch( Exception ex ) {
			Log.d("ObmBookingVehicleItemDAO", "createTable:exception:"+ex);
		} finally {
			db.close();
		}
	}	
	
	/**
	 * <p>Insert Or Update a record of BookingVehicleItem</p>
	 * 
	 * @param ObmBookingVehicleItems
	 * @return
	 */
	public long insertOrUpdate(ObmBookingVehicleItem[] ObmBookingVehicleItems) {
		long latestTime = PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_ITEM_LAST_TIME);
		for (ObmBookingVehicleItem ObmBookingVehicleItem : ObmBookingVehicleItems) {
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_BOOKING_VEHICLE_ITEM_ID, ObmBookingVehicleItem.getBookingVehicleItemId());
			cv.put(COLUMN_BOOKING_VEHICLE_ID, ObmBookingVehicleItem.getBookingVehicleId());
			cv.put(COLUMN_BOOKING_NUMBER, ObmBookingVehicleItem.getBookingNumber());
			cv.put(COLUMN_BOOKING_SERVICE, ObmBookingVehicleItem.getBookingService());
			cv.put(COLUMN_BOOKING_SERVICE_CD, ObmBookingVehicleItem.getBookingServiceCd());
			cv.put(COLUMN_FLIGHT_NUMBER, ObmBookingVehicleItem.getFlightNumber());
			cv.put(COLUMN_PICKUP_CITY, ObmBookingVehicleItem.getPickupCity());
			cv.put(COLUMN_PICKUP_CITY_CD, ObmBookingVehicleItem.getPickupCityCd());
			cv.put(COLUMN_PICKUP_ADDRESS, ObmBookingVehicleItem.getPickupAddress());
			cv.put(COLUMN_PICKUP_POINT, ObmBookingVehicleItem.getPickupPoint());
			cv.put(COLUMN_DESTINATION, ObmBookingVehicleItem.getDestination());
			cv.put(COLUMN_VEHICLE, ObmBookingVehicleItem.getVehicle());
			cv.put(COLUMN_VEHICLE_CD, ObmBookingVehicleItem.getVehicleCd());
			cv.put(COLUMN_DRIVER_USER_ID, ObmBookingVehicleItem.getDriverUserId());
			cv.put(COLUMN_LEAD_PASSENGER_FIRST_NAME, ObmBookingVehicleItem.getLeadPassengerFirstName());
			cv.put(COLUMN_LEAD_PASSENGER_LAST_NAME, ObmBookingVehicleItem.getLeadPassengerLastName());
			cv.put(COLUMN_LEAD_PASSENGER_MOBILE_NUMBER, ObmBookingVehicleItem.getLeadPassengerMobileNumber());
			cv.put(COLUMN_BOOKING_STATUS, ObmBookingVehicleItem.getBookingStatus());
			cv.put(COLUMN_BOOKING_STATUS_CD, ObmBookingVehicleItem.getBookingServiceCd());
			cv.put(COLUMN_PAYMENT_STATUS, ObmBookingVehicleItem.getPaymentStatus());
			cv.put(COLUMN_PAYMENT_STATUS_CD, ObmBookingVehicleItem.getPaymentStatusCd());
			cv.put(COLUMN_DRIVER_USER_NAME, ObmBookingVehicleItem.getDriverUserName());
			cv.put(COLUMN_DRIVER_MOBILE_NUMBER, ObmBookingVehicleItem.getDriverMobileNumber());
			cv.put(COLUMN_DRIVER_VEHICLE, ObmBookingVehicleItem.getDriverVehicle());
			cv.put(COLUMN_LEAD_PASSENGER_GENDER, ObmBookingVehicleItem.getLeadPassengerGender());
			cv.put(COLUMN_LEAD_PASSENGER_GENDER_CD, ObmBookingVehicleItem.getLeadPassengerGenderCd());
			cv.put(COLUMN_NUMBER_OF_PASSENGER, ObmBookingVehicleItem.getNumberOfPassenger());
			cv.put(COLUMN_PAYMENT_MODE, ObmBookingVehicleItem.getPaymentMode());
			cv.put(COLUMN_PAYMENT_MODE_CD, ObmBookingVehicleItem.getPaymentModeCd());
			cv.put(COLUMN_BOOKING_USER_NAME, ObmBookingVehicleItem.getBookingUserName());
			cv.put(COLUMN_BOOKING_USER_MOBILE_NUMBER, ObmBookingVehicleItem.getBookingUserMobileNumber());
			cv.put(COLUMN_BOOKING_USER_EMAIL, ObmBookingVehicleItem.getBookingUserEmail());
			cv.put(COLUMN_BOOKING_USER_SURNAME, ObmBookingVehicleItem.getBookingUserSurname());
			cv.put(COLUMN_ORG_EMAIL, ObmBookingVehicleItem.getOrgEmail());
			cv.put(COLUMN_ASSIGN_ORG_ID, ObmBookingVehicleItem.getAssignOrgId());
			cv.put(COLUMN_ASSIGN_ORG_NAME, ObmBookingVehicleItem.getAssignOrgName());
			cv.put(COLUMN_ASSIGN_ORG_EMAIL, ObmBookingVehicleItem.getAssignOrgEmail());
			cv.put(COLUMN_ASSIGN_DRIVER_USER_ID, ObmBookingVehicleItem.getAssignDriverUserId());
			cv.put(COLUMN_ASSIGN_DRIVER_USER_NAME, ObmBookingVehicleItem.getAssignDriverUserName());
			cv.put(COLUMN_ASSIGN_DRIVER_MOBILE_PHONE, ObmBookingVehicleItem.getAssignDriverMobilePhone());
			cv.put(COLUMN_ASSIGN_DRIVER_EMAIL, ObmBookingVehicleItem.getAssignDriverEmail());
			cv.put(COLUMN_BOOKING_USER_GENDER, ObmBookingVehicleItem.getBookingUserGender());
			cv.put(COLUMN_BOOKING_USER_GENDER_CD, ObmBookingVehicleItem.getBookingUserGenderCd());
			cv.put(COLUMN_AGENT_USER_ID, ObmBookingVehicleItem.getAgentUserId());
			cv.put(COLUMN_AGENT_USER_NAME, ObmBookingVehicleItem.getAgentUserName());
			cv.put(COLUMN_AGENT_MOBILE_NUMBER, ObmBookingVehicleItem.getAgentMobileNumber());
			cv.put(COLUMN_AGENT_EMAIL, ObmBookingVehicleItem.getAgentEmail());
			cv.put(COLUMN_OPERATOR_MOBILE_NUMBER, ObmBookingVehicleItem.getOperatorMobileNumber());
			cv.put(COLUMN_OPERATOR_EMAIL, ObmBookingVehicleItem.getOperatorEmail());
			cv.put(COLUMN_REMARK, ObmBookingVehicleItem.getRemark());
			cv.put(COLUMN_ORG_ID, ObmBookingVehicleItem.getOrgId());
			cv.put(COLUMN_ORG_NAME, ObmBookingVehicleItem.getOrgName());
			cv.put(COLUMN_OPERATOR_ID, ObmBookingVehicleItem.getOperatorId());
			cv.put(COLUMN_OPERATOR_NAME, ObmBookingVehicleItem.getOperatorName());
			cv.put(COLUMN_DATA_STATE, ObmBookingVehicleItem.getDataState());
			cv.put(COLUMN_SEND_STATE, ObmBookingVehicleItem.getSendState());
			cv.put(COLUMN_PRICE, ObmBookingVehicleItem.getPrice());
			cv.put(COLUMN_BOOKING_GHOURS, ObmBookingVehicleItem.getBookingHours());
			cv.put(COLUMN_DISPLAY_NO, ObmBookingVehicleItem.getDisplayNo());
			cv.put(COLUMN_PICKUP_DATE, ObmBookingVehicleItem.getPickupDate().getTime());
			
			cv.put(COLUMN_PICKUP_TIME, ObmBookingVehicleItem.getPickupTime());
			cv.put(COLUMN_PICKUP_DATE_TIME, DateUtil.getDate(ObmBookingVehicleItem.getPickupDate(), ObmBookingVehicleItem.getPickupTime()).getTime());
			cv.put(COLUMN_CREATE_DATE_TIME, ObmBookingVehicleItem.getCreateDateTime().getTime());
			cv.put(COLUMN_MODIFY_TIMESTAMP, ObmBookingVehicleItem.getModifyTimestamp().getTime());
			if (ObmBookingVehicleItem.getSendDateTime()!=null) {
				cv.put(COLUMN_SEND_DATE_TIME, ObmBookingVehicleItem.getSendDateTime().getTime());
			}
			if (ObmBookingVehicleItem.getRecieveDateTime()!=null) {
				cv.put(COLUMN_RECIEVE_DATE_TIME, ObmBookingVehicleItem.getRecieveDateTime().getTime());
			}
			super.insertOrUpdate(cv, COLUMN_BOOKING_VEHICLE_ITEM_ID + " = ?", new String[]{ObmBookingVehicleItem.getBookingVehicleItemId()});
			if (latestTime<ObmBookingVehicleItem.getModifyTimestamp().getTime()) {
				latestTime = ObmBookingVehicleItem.getModifyTimestamp().getTime();
			}
		}
		return latestTime;
	}
	
	/**
	 * <p>Insert Or Update a record of BookingVehicleItem</p>
	 * 
	 * @param values
	 * @param articleId
	 * @return
	 */
	public boolean insertOrUpdate(ContentValues values, String bookingVehicleItemId) {
		return super.insertOrUpdateWithTime(values, COLUMN_BOOKING_VEHICLE_ITEM_ID + " = ?", new String[]{bookingVehicleItemId});
	}

	/**
	 * <p>Get all the ObmBookingVehicleItem data</p>
	 * 
	 * @return List<ObmBookingVehicleItem>
	 */
	public List<ObmBookingVehicleItem> getObmBookingVehicleItems(String driverUserId, String flag) {
		String sql = "SELECT * FROM " + TABLE_NAME;
		if (StringUtil.isNotEmpty(driverUserId)) {
			Date currentDate = new Date();
			long currentMilliseconds = currentDate.getTime();
			if (FLAG_UPCOMING.equalsIgnoreCase(flag)) {
				sql += " where (" + COLUMN_DRIVER_USER_ID + "='" + driverUserId + "' or " + COLUMN_OPERATOR_ID + "='" + driverUserId + "') and " +
						COLUMN_PICKUP_DATE_TIME + " >=  " + currentMilliseconds  + " order by " + COLUMN_PICKUP_DATE + " asc," + COLUMN_PICKUP_TIME + " asc";    
			} else if (FLAG_PAST.equalsIgnoreCase(flag)) {
				sql += " where (" + COLUMN_DRIVER_USER_ID + "='" + driverUserId + "' or " + COLUMN_OPERATOR_ID + "='" + driverUserId + "') and " +
						COLUMN_PICKUP_DATE_TIME + " <  " +  currentMilliseconds + " order by " + COLUMN_PICKUP_DATE + " desc," + COLUMN_PICKUP_TIME + " asc";  
			} else {
				sql += " where (" + COLUMN_DRIVER_USER_ID + "='" + driverUserId + "' or " + COLUMN_OPERATOR_ID + "='" + driverUserId + "') order by " +
						COLUMN_PICKUP_DATE + " asc," + COLUMN_PICKUP_TIME + " asc";
			}
		}
		return getObmBookingVehicleItemsBySql(sql);
	}
	
	/**
	 * <p>getObmBookingVehicleItems</p>
	 * 
	 * @param driverUserId
	 * @param from
	 * @param to 
	 * @return
	 */
	public List<ObmBookingVehicleItem> getObmBookingVehicleItems(String driverUserId, long from, long to) {
		String sql = "SELECT * FROM " + TABLE_NAME;
		if (StringUtil.isNotEmpty(driverUserId)) {
			sql += " where (" + COLUMN_DRIVER_USER_ID + "='" + driverUserId + "' or " + COLUMN_OPERATOR_ID + "='" + driverUserId + "') and " +
						COLUMN_PICKUP_DATE_TIME + " >=  " +  from +  " and " + COLUMN_PICKUP_DATE_TIME + " <=" + to +
						" order by " + COLUMN_PICKUP_DATE + " desc," + COLUMN_PICKUP_TIME + " asc";  
		}
		return getObmBookingVehicleItemsBySql(sql);
	}
	
	/**
	 * <p>Get ObmBookingVehicleItems By Sql</p>
	 * 
	 * @param sql
	 * @return
	 */
	private List<ObmBookingVehicleItem> getObmBookingVehicleItemsBySql(String sql) {
		List<ObmBookingVehicleItem> ObmBookingVehicleItems = new LinkedList<ObmBookingVehicleItem>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(sql, null);
			if( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();

				while(cursor.isAfterLast()==false) {
					ObmBookingVehicleItem ObmBookingVehicleItem = new ObmBookingVehicleItem();
					setObmBookingVehicleItem(ObmBookingVehicleItem, cursor);
					
					ObmBookingVehicleItems.add(ObmBookingVehicleItem);
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
		return ObmBookingVehicleItems;
	}
	/**
	 * <p>Get a ObmBookingVehicleItem object</p>
	 * 
	 * @param articleId
	 * @return ObmBookingVehicleItem
	 */
	public ObmBookingVehicleItem getObmBookingVehicleItem(String bookingVehicleItemId) {
		String query = "SELECT * FROM " + TABLE_NAME + " where " + COLUMN_BOOKING_VEHICLE_ITEM_ID + "='" + bookingVehicleItemId + "'";
		Log.d(TAG, "query:"+query);
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ObmBookingVehicleItem ObmBookingVehicleItem = new ObmBookingVehicleItem();
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(query, null);
			if (cursor!=null && cursor.getCount()>0) {
				cursor.moveToFirst();
				setObmBookingVehicleItem(ObmBookingVehicleItem, cursor);
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
		return ObmBookingVehicleItem;
	}
	
	/**
	 * <p>Get all the ObmBookingVehicleItem ID</p>
	 * 
	 * @return 
	 */
	public List<String> getBookingVehicleItemIds() {
		String query = "SELECT " + COLUMN_BOOKING_VEHICLE_ITEM_ID + " FROM " + TABLE_NAME;
		Log.d(TAG, "query:"+query);
		SQLiteDatabase db = null;
		Cursor cursor = null;
		List<String> articleIds = new ArrayList<String>();
		try {
			db = this.getDatabase();
			cursor = db.rawQuery(query, null);
			if (cursor!=null && cursor.getCount()>0) {
				cursor.moveToFirst();
				articleIds.add(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_VEHICLE_ITEM_ID)));
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
		return articleIds;
	}
	
	/**
	 * <p>Delete table data by articleId</p>
	 * 
	 * @param articleId
	 * @return
	 */
	public boolean deleteByProductId(String bookingVehicleItemId) {
		return super.delete(COLUMN_BOOKING_VEHICLE_ITEM_ID + " = ?", new String[]{bookingVehicleItemId});
	}
	
	@Override
	public boolean delete() {
		super.mTableName = TABLE_NAME;
		return super.delete();
	}
	
	/**
	 * <p>Set ObmBookingVehicleItem record data to ObmBookingVehicleItem object</p>
	 * 
	 * @param ObmBookingVehicleItem
	 * @param cursor
	 */
	private void setObmBookingVehicleItem(ObmBookingVehicleItem ObmBookingVehicleItem, Cursor cursor) {
		if (ObmBookingVehicleItem == null) {
			ObmBookingVehicleItem = new ObmBookingVehicleItem();
		}
		
		ObmBookingVehicleItem.setBookingVehicleItemId(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_VEHICLE_ITEM_ID)));
		ObmBookingVehicleItem.setBookingVehicleId(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_VEHICLE_ID)));
		ObmBookingVehicleItem.setBookingNumber(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_NUMBER)));
		ObmBookingVehicleItem.setBookingService(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_SERVICE)));
		ObmBookingVehicleItem.setBookingServiceCd(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_SERVICE_CD)));
		ObmBookingVehicleItem.setFlightNumber(cursor.getString(cursor.getColumnIndex(COLUMN_FLIGHT_NUMBER)));
		ObmBookingVehicleItem.setPickupCity(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_CITY)));
		ObmBookingVehicleItem.setPickupCityCd(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_CITY_CD)));
		ObmBookingVehicleItem.setPickupAddress(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_ADDRESS)));
		ObmBookingVehicleItem.setPickupPoint(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_POINT)));
		ObmBookingVehicleItem.setDestination(cursor.getString(cursor.getColumnIndex(COLUMN_DESTINATION)));
		ObmBookingVehicleItem.setVehicle(cursor.getString(cursor.getColumnIndex(COLUMN_VEHICLE)));
		ObmBookingVehicleItem.setVehicleCd(cursor.getString(cursor.getColumnIndex(COLUMN_VEHICLE_CD)));
		
		ObmBookingVehicleItem.setLeadPassengerFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_FIRST_NAME)));
		ObmBookingVehicleItem.setLeadPassengerLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_LAST_NAME)));
		ObmBookingVehicleItem.setLeadPassengerMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_MOBILE_NUMBER)));
		ObmBookingVehicleItem.setBookingStatus(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_STATUS)));
		ObmBookingVehicleItem.setBookingServiceCd(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_STATUS_CD)));
		ObmBookingVehicleItem.setPaymentStatus(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_STATUS)));
		ObmBookingVehicleItem.setPaymentStatusCd(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_STATUS_CD)));
		ObmBookingVehicleItem.setDriverUserName(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_USER_NAME)));
		ObmBookingVehicleItem.setDriverMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_MOBILE_NUMBER)));
		ObmBookingVehicleItem.setDriverVehicle(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_VEHICLE)));
		ObmBookingVehicleItem.setLeadPassengerGender(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_GENDER)));
		ObmBookingVehicleItem.setLeadPassengerGenderCd(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_GENDER_CD)));
		ObmBookingVehicleItem.setNumberOfPassenger(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER_OF_PASSENGER)));
		ObmBookingVehicleItem.setPaymentMode(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_MODE)));
		ObmBookingVehicleItem.setPaymentModeCd(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_MODE_CD)));
		ObmBookingVehicleItem.setBookingUserName(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_NAME)));
		ObmBookingVehicleItem.setBookingUserMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_MOBILE_NUMBER)));
		ObmBookingVehicleItem.setBookingUserEmail(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_EMAIL)));
		ObmBookingVehicleItem.setBookingUserSurname(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_SURNAME)));
		ObmBookingVehicleItem.setOrgEmail(cursor.getString(cursor.getColumnIndex(COLUMN_ORG_EMAIL)));
		ObmBookingVehicleItem.setOrgId(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_ORG_ID)));
		ObmBookingVehicleItem.setAssignOrgName(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_ORG_NAME)));
		ObmBookingVehicleItem.setAssignOrgEmail(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_ORG_EMAIL)));
		ObmBookingVehicleItem.setAssignDriverUserId(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_USER_ID)));
		ObmBookingVehicleItem.setAssignDriverUserName(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_USER_NAME)));
		ObmBookingVehicleItem.setAssignDriverMobilePhone(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_MOBILE_PHONE)));
		ObmBookingVehicleItem.setAssignDriverEmail(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_EMAIL)));
		ObmBookingVehicleItem.setBookingUserGender(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_GENDER)));
		ObmBookingVehicleItem.setBookingUserGenderCd(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_GENDER_CD)));
		ObmBookingVehicleItem.setAgentUserId(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_USER_ID)));
		ObmBookingVehicleItem.setAgentUserName(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_USER_NAME)));
		ObmBookingVehicleItem.setAgentMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_MOBILE_NUMBER)));
		ObmBookingVehicleItem.setAgentEmail(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_EMAIL)));
		ObmBookingVehicleItem.setOperatorMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_OPERATOR_MOBILE_NUMBER)));
		ObmBookingVehicleItem.setOperatorEmail(cursor.getString(cursor.getColumnIndex(COLUMN_OPERATOR_EMAIL)));
		ObmBookingVehicleItem.setRemark(cursor.getString(cursor.getColumnIndex(COLUMN_REMARK)));
		ObmBookingVehicleItem.setOrgId(cursor.getString(cursor.getColumnIndex(COLUMN_ORG_ID)));
		ObmBookingVehicleItem.setOrgName(cursor.getString(cursor.getColumnIndex(COLUMN_ORG_NAME)));
		ObmBookingVehicleItem.setOperatorId(cursor.getString(cursor.getColumnIndex(COLUMN_OPERATOR_ID)));
		ObmBookingVehicleItem.setOperatorName(cursor.getString(cursor.getColumnIndex(COLUMN_OPERATOR_NAME)));
		ObmBookingVehicleItem.setDataState(cursor.getString(cursor.getColumnIndex(COLUMN_DATA_STATE)));
		ObmBookingVehicleItem.setSendState(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_STATE)));
		
		ObmBookingVehicleItem.setPrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_PRICE)));
		ObmBookingVehicleItem.setBookingHours(cursor.getFloat(cursor.getColumnIndex(COLUMN_BOOKING_GHOURS)));
		ObmBookingVehicleItem.setDisplayNo(cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_NO)));
		ObmBookingVehicleItem.setPickupDate(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_PICKUP_DATE))));
		ObmBookingVehicleItem.setPickupTime(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_TIME)));
		ObmBookingVehicleItem.setPickupDateTime(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_PICKUP_DATE_TIME))));
		ObmBookingVehicleItem.setModifyTimestamp(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_MODIFY_TIMESTAMP))));
		ObmBookingVehicleItem.setSendDateTime(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_SEND_DATE_TIME))));
		ObmBookingVehicleItem.setRecieveDateTime(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_RECIEVE_DATE_TIME))));
	}
	
	/**
	 * <p>Insert or update column's value base on bookingVehicleItemId</p>
	 * 
	 * @param bookingVehicleItemId
	 * @param collumnName
	 * @param value
	 */
	public static void insertOrUpdate(String bookingVehicleItemId, String columnName, String value) {
		ObmBookingVehicleItemDAO ObmBookingVehicleItemDao = (ObmBookingVehicleItemDAO) (ObsDaoFactory.getInstance().getDAO(DAOType.OBM_BOOKING_VEHICLE_ITEM, ObsApplication.getAppContext()));
		ContentValues cv = new ContentValues();
		cv.put(columnName, value);
		ObmBookingVehicleItemDao.insertOrUpdate(cv, bookingVehicleItemId);
	}
	
	
	/**
	 * <p>Get ObmBookingVehicleItem DAO</p>
	 */
	public static ObmBookingVehicleItemDAO getInstance() {
		return (ObmBookingVehicleItemDAO) (ObsDaoFactory.getInstance().getDAO(DAOType.OBM_BOOKING_VEHICLE_ITEM, ObsApplication.getAppContext()));
	}
	
}
