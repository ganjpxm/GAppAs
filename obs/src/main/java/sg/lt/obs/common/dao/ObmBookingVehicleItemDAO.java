/**
 * ObmBookingVehicleItemDAO.java
 * 
 * Created by Gan Jianping on 20/07/13.
 * Copyright (c) 2013 DBS. All rights reserved.
 */
package sg.lt.obs.common.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ganjp.glib.core.util.StringUtil;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.dao.ObsDaoFactory.DAOType;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.PreferenceUtil;

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
	public static final String  COLUMN_BOOKING_NUMBER               = "bookingNumber";
    public static final String  COLUMN_PICKUP_DATE                  = "pickupDate";
    public static final String  COLUMN_PICKUP_TIME                  = "pickupTime";
    public static final String  COLUMN_PICKUP_DATE_TIME             = "pickupDateTime";
    public static final String  COLUMN_BOOKING_SERVICE              = "bookingService";
    public static final String  COLUMN_BOOKING_SERVICE_CD           = "bookingServiceCd";
	public static final String  COLUMN_BOOKING_HOURS                = "bookingHours";
	public static final String  COLUMN_FLIGHT_NUMBER                = "flightNumber";
	public static final String  COLUMN_PICKUP_ADDRESS               = "pickupAddress";
	public static final String  COLUMN_DESTINATION                  = "destination";
    public static final String  COLUMN_STOP1_ADDRESS                = "stop1Address";
    public static final String  COLUMN_STOP2_ADDRESS                = "stop2Address";
    public static final String  COLUMN_REMARK                       = "remark";

    public static final String  COLUMN_VEHICLE                      = "vehicle";
    public static final String  COLUMN_PRICE                        = "price";
    public static final String  COLUMN_PAYMENT_STATUS               = "paymentStatus";
    public static final String  COLUMN_PAYMENT_MODE                 = "paymentMode";
    public static final String  COLUMN_BOOKING_STATUS               = "bookingStatus";

    public static final String  COLUMN_BOOKING_USER_FIRST_NAME     = "bookingUserFirstName";
    public static final String  COLUMN_BOOKING_USER_LAST_NAME      = "bookingUserLastName";
    public static final String  COLUMN_BOOKING_USER_GENDER         = "bookingUserGender";
    public static final String  COLUMN_BOOKING_USER_MOBILE_NUMBER  = "bookingUserMobileNumber";
    public static final String  COLUMN_BOOKING_USER_EMAIL          = "bookingUserEmail";

    public static final String  COLUMN_LEAD_PASSENGER_FIRST_NAME    = "leadPassengerFirstName";
    public static final String  COLUMN_LEAD_PASSENGER_LAST_NAME     = "leadPassengerLastName";
    public static final String  COLUMN_LEAD_PASSENGER_GENDER        = "leadPassengerGender";
    public static final String  COLUMN_LEAD_PASSENGER_MOBILE_NUMBER = "leadPassengerMobileNumber";
    public static final String  COLUMN_LEAD_PASSENGER_EMAIL         = "leadPassengerEmail";
    public static final String  COLUMN_NUMBER_OF_PASSENGER          = "numberOfPassenger";

	public static final String  COLUMN_DRIVER_USER_NAME            = "driverUserName";
    public static final String  COLUMN_DRIVER_LOGIN_USER_ID        = "driverLoginUserId";
	public static final String  COLUMN_DRIVER_MOBILE_NUMBER        = "driverMobileNumber";
	public static final String  COLUMN_DRIVER_VEHICLE              = "driverVehicle";
    public static final String  COLUMN_DRIVER_CLAIM_CURRENCY       = "driverClaimCurrency";
    public static final String  COLUMN_DRIVER_CLAIM_PRICE          = "driverClaimPrice";
    public static final String  COLUMN_DRIVER_ACTION               = "driverAction";

    public static final String  COLUMN_ASSIGN_DRIVER_USER_ID       = "assignDriverUserId";
    public static final String  COLUMN_ASSIGN_DRIVER_USER_NAME     = "assignDriverUserName";

    public static final String  COLUMN_HISTORTY_DRIVER_USER_IDS    = "historyDriverUserIds";
    public static final String  COLUMN_BROADCAST_TAG               = "broadcastTag";

	public static final String  COLUMN_CREATE_DATE_TIME            = "createDateTime";
	public static final String  COLUMN_MODIFY_TIMESTAMP            = "modifyTimestamp";
	public static final String  COLUMN_DATA_STATE                  = "dataState";

   	//Field name : NULL, INTEGER, REAL, TEXT, BLOB
	protected static final String createSQL = new StringBuffer().append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (")
			.append(COLUMN_BOOKING_VEHICLE_ITEM_ID).append(" TEXT primary key, ")
			.append(COLUMN_BOOKING_NUMBER).append(" TEXT, ")
            .append(COLUMN_PICKUP_DATE).append(" INTEGER, ")
            .append(COLUMN_PICKUP_TIME).append(" TEXT, ")
            .append(COLUMN_PICKUP_DATE_TIME).append(" INTEGER, ")
			.append(COLUMN_BOOKING_SERVICE).append(" TEXT, ")
            .append(COLUMN_BOOKING_SERVICE_CD).append(" TEXT, ")
            .append(COLUMN_BOOKING_HOURS).append(" INTEGER, ")
			.append(COLUMN_FLIGHT_NUMBER).append(" TEXT, ")
			.append(COLUMN_PICKUP_ADDRESS).append(" TEXT, ")
			.append(COLUMN_DESTINATION).append(" TEXT, ")
            .append(COLUMN_STOP1_ADDRESS).append(" TEXT, ")
            .append(COLUMN_STOP2_ADDRESS).append(" TEXT, ")
            .append(COLUMN_REMARK).append(" TEXT, ")

			.append(COLUMN_VEHICLE).append(" TEXT, ")
            .append(COLUMN_PRICE).append(" INTEGER, ")
            .append(COLUMN_PAYMENT_STATUS).append(" TEXT, ")
            .append(COLUMN_PAYMENT_MODE).append(" TEXT, ")
            .append(COLUMN_BOOKING_STATUS).append(" TEXT, ")

            .append(COLUMN_BOOKING_USER_FIRST_NAME).append(" TEXT, ")
            .append(COLUMN_BOOKING_USER_LAST_NAME).append(" TEXT, ")
            .append(COLUMN_BOOKING_USER_GENDER).append(" TEXT, ")
            .append(COLUMN_BOOKING_USER_MOBILE_NUMBER).append(" TEXT, ")
            .append(COLUMN_BOOKING_USER_EMAIL).append(" TEXT, ")

			.append(COLUMN_LEAD_PASSENGER_FIRST_NAME).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_LAST_NAME).append(" TEXT, ")
            .append(COLUMN_LEAD_PASSENGER_GENDER).append(" TEXT, ")
			.append(COLUMN_LEAD_PASSENGER_MOBILE_NUMBER).append(" TEXT, ")
            .append(COLUMN_LEAD_PASSENGER_EMAIL).append(" TEXT, ")
            .append(COLUMN_NUMBER_OF_PASSENGER).append(" TEXT, ")

			.append(COLUMN_DRIVER_USER_NAME).append(" TEXT, ")
            .append(COLUMN_DRIVER_LOGIN_USER_ID).append(" TEXT, ")
			.append(COLUMN_DRIVER_MOBILE_NUMBER).append(" TEXT, ")
			.append(COLUMN_DRIVER_VEHICLE).append(" TEXT, ")
            .append(COLUMN_DRIVER_CLAIM_CURRENCY).append(" TEXT, ")
            .append(COLUMN_DRIVER_CLAIM_PRICE).append(" INTEGER, ")
            .append(COLUMN_DRIVER_ACTION).append(" TEXT, ")

            .append(COLUMN_ASSIGN_DRIVER_USER_ID).append(" TEXT, ")
            .append(COLUMN_ASSIGN_DRIVER_USER_NAME).append(" TEXT, ")

            .append(COLUMN_HISTORTY_DRIVER_USER_IDS).append(" TEXT, ")
            .append(COLUMN_BROADCAST_TAG).append(" TEXT, ")

            .append(COLUMN_CREATE_DATE_TIME).append(" INTEGER, ")
            .append(COLUMN_MODIFY_TIMESTAMP).append(" INTEGER, ")
            .append(COLUMN_DATA_STATE).append(" TEXT)")
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
		long updateLatestTime = PreferenceUtil.getLong(ObsConst.KEY_PREFERENCE_BOOKING_VEHICLE_UPDATE_ITEM_LAST_TIME);
		for (ObmBookingVehicleItem obmBookingVehicleItem : ObmBookingVehicleItems) {
			ContentValues cv = new ContentValues();

            cv.put(COLUMN_BOOKING_VEHICLE_ITEM_ID, obmBookingVehicleItem.getBookingVehicleItemId());
			cv.put(COLUMN_BOOKING_NUMBER, obmBookingVehicleItem.getBookingNumber());
            cv.put(COLUMN_PICKUP_DATE, obmBookingVehicleItem.getPickupDate().getTime());
            cv.put(COLUMN_PICKUP_TIME, obmBookingVehicleItem.getPickupTime());
            cv.put(COLUMN_PICKUP_DATE_TIME, obmBookingVehicleItem.getPickupDateTime().getTime());
			cv.put(COLUMN_BOOKING_SERVICE, obmBookingVehicleItem.getBookingService());
            cv.put(COLUMN_BOOKING_SERVICE_CD, obmBookingVehicleItem.getBookingServiceCd());
            cv.put(COLUMN_BOOKING_HOURS, obmBookingVehicleItem.getBookingHours());
			cv.put(COLUMN_FLIGHT_NUMBER, obmBookingVehicleItem.getFlightNumber());
			cv.put(COLUMN_PICKUP_ADDRESS, obmBookingVehicleItem.getPickupAddress());
			cv.put(COLUMN_DESTINATION, obmBookingVehicleItem.getDestination());
            cv.put(COLUMN_STOP1_ADDRESS, obmBookingVehicleItem.getStop1Address());
            cv.put(COLUMN_STOP2_ADDRESS, obmBookingVehicleItem.getStop2Address());
            cv.put(COLUMN_REMARK, obmBookingVehicleItem.getRemark());

            cv.put(COLUMN_VEHICLE, obmBookingVehicleItem.getVehicle());
            cv.put(COLUMN_PRICE, obmBookingVehicleItem.getPrice());
            cv.put(COLUMN_PAYMENT_STATUS, obmBookingVehicleItem.getPaymentStatus());
            cv.put(COLUMN_PAYMENT_MODE, obmBookingVehicleItem.getPaymentMode());
            cv.put(COLUMN_BOOKING_STATUS, obmBookingVehicleItem.getBookingStatus());

            cv.put(COLUMN_BOOKING_USER_FIRST_NAME, obmBookingVehicleItem.getBookingUserFirstName());
            cv.put(COLUMN_BOOKING_USER_LAST_NAME, obmBookingVehicleItem.getBookingUserLastName());
            cv.put(COLUMN_BOOKING_USER_GENDER, obmBookingVehicleItem.getBookingUserGender());
            cv.put(COLUMN_BOOKING_USER_MOBILE_NUMBER, obmBookingVehicleItem.getBookingUserMobileNumber());
            cv.put(COLUMN_BOOKING_USER_EMAIL, obmBookingVehicleItem.getBookingUserEmail());

			cv.put(COLUMN_LEAD_PASSENGER_FIRST_NAME, obmBookingVehicleItem.getLeadPassengerFirstName());
			cv.put(COLUMN_LEAD_PASSENGER_LAST_NAME, obmBookingVehicleItem.getLeadPassengerLastName());
            cv.put(COLUMN_LEAD_PASSENGER_GENDER, obmBookingVehicleItem.getLeadPassengerGender());
			cv.put(COLUMN_LEAD_PASSENGER_MOBILE_NUMBER, obmBookingVehicleItem.getLeadPassengerMobileNumber());
            cv.put(COLUMN_LEAD_PASSENGER_EMAIL, obmBookingVehicleItem.getLeadPassengerEmail());
            cv.put(COLUMN_NUMBER_OF_PASSENGER, obmBookingVehicleItem.getNumberOfPassenger());

			cv.put(COLUMN_DRIVER_USER_NAME, obmBookingVehicleItem.getDriverUserName());
            cv.put(COLUMN_DRIVER_LOGIN_USER_ID, obmBookingVehicleItem.getDriverLoginUserId());
			cv.put(COLUMN_DRIVER_MOBILE_NUMBER, obmBookingVehicleItem.getDriverMobileNumber());
			cv.put(COLUMN_DRIVER_VEHICLE, obmBookingVehicleItem.getDriverVehicle());
            cv.put(COLUMN_DRIVER_CLAIM_CURRENCY, obmBookingVehicleItem.getDriverClaimCurrency());
            cv.put(COLUMN_DRIVER_CLAIM_PRICE, obmBookingVehicleItem.getDriverClaimPrice());
            cv.put(COLUMN_DRIVER_ACTION, obmBookingVehicleItem.getDriverAction());

            cv.put(COLUMN_ASSIGN_DRIVER_USER_ID, obmBookingVehicleItem.getAssignDriverUserId());
            cv.put(COLUMN_ASSIGN_DRIVER_USER_NAME, obmBookingVehicleItem.getAssignDriverUserName());

            cv.put(COLUMN_HISTORTY_DRIVER_USER_IDS, obmBookingVehicleItem.getHistoryDriverUserIds());
            cv.put(COLUMN_BROADCAST_TAG, obmBookingVehicleItem.getBroadcastTag());

			cv.put(COLUMN_CREATE_DATE_TIME, obmBookingVehicleItem.getCreateDateTime().getTime());
			cv.put(COLUMN_MODIFY_TIMESTAMP, obmBookingVehicleItem.getModifyTimestamp().getTime());
            cv.put(COLUMN_DATA_STATE, obmBookingVehicleItem.getDataState());

			super.insertOrUpdate(cv, COLUMN_BOOKING_VEHICLE_ITEM_ID + " = ?", new String[]{obmBookingVehicleItem.getBookingVehicleItemId()});
			if (updateLatestTime<obmBookingVehicleItem.getModifyTimestamp().getTime()) {
                updateLatestTime = obmBookingVehicleItem.getModifyTimestamp().getTime();
			}
		}
		return updateLatestTime;
	}
	
	/**
	 * <p>Insert Or Update a record of BookingVehicleItem</p>
	 * 
	 * @param values
	 * @param bookingVehicleItemId
	 * @return
	 */
	public boolean insertOrUpdate(ContentValues values, String bookingVehicleItemId) {
		return super.insertOrUpdateWithTime(values, COLUMN_BOOKING_VEHICLE_ITEM_ID + " = ?", new String[]{bookingVehicleItemId});
	}

    /**
     * <p>Get broadcast the ObmBookingVehicleItem data</p>
     *
     * @return List<ObmBookingVehicleItem>
     */
    public List<ObmBookingVehicleItem> getBroadcastObmBookingVehicleItems() {
        String sql = "SELECT * FROM " + TABLE_NAME + " where " + COLUMN_BROADCAST_TAG + " = 'yes' " + " order by " + COLUMN_PICKUP_DATE
                + " desc," + COLUMN_PICKUP_TIME + " asc";
        return getObmBookingVehicleItemsBySql(sql);
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
            sql += " where driverLoginUserId = '" + driverUserId + "' ";
            if (FLAG_UPCOMING.equalsIgnoreCase(flag)) {
				sql += " and " + COLUMN_PICKUP_DATE_TIME + " >=  " + currentMilliseconds + " order by " + COLUMN_PICKUP_DATE + " asc," + COLUMN_PICKUP_TIME + " asc";
			} else if (FLAG_PAST.equalsIgnoreCase(flag)) {
				sql += " and " + COLUMN_PICKUP_DATE_TIME + " <  " +  currentMilliseconds + " order by " + COLUMN_PICKUP_DATE + " desc," + COLUMN_PICKUP_TIME + " asc";
			} else {
                sql += " order by " + COLUMN_PICKUP_DATE + " desc," + COLUMN_PICKUP_TIME + " asc";
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
			sql += " where " + COLUMN_PICKUP_DATE_TIME + " >=  " +  from +  " and " + COLUMN_PICKUP_DATE_TIME + " <=" + to +
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
     * <p>Get BookingVehicleItems By Sql</p>
     *
     * @return
     */
     public List<Map<String,String>> getAllBookingVehicleItem() {
        String sql = "SELECT * FROM " + TABLE_NAME + " order by " + COLUMN_PICKUP_DATE + " desc," + COLUMN_PICKUP_TIME + " desc";
        List<Map<String,String>> maps = new LinkedList<Map<String,String>>();

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getDatabase();
            cursor = db.rawQuery(sql, null);
            if( cursor != null && cursor.getCount() > 0 ) {
                cursor.moveToFirst();

                while(cursor.isAfterLast()==false) {
                    Map<String,String> map = new HashMap<String,String>();
                    map.put(COLUMN_BOOKING_VEHICLE_ITEM_ID, cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_VEHICLE_ITEM_ID)));
                    map.put(COLUMN_DRIVER_LOGIN_USER_ID, cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_LOGIN_USER_ID)));
                    map.put(COLUMN_ASSIGN_DRIVER_USER_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_USER_ID)));
                    map.put(COLUMN_BROADCAST_TAG, cursor.getString(cursor.getColumnIndex(COLUMN_BROADCAST_TAG)));
                    maps.add(map);
                    cursor.moveToNext();
                }
            }
        } catch( Exception ex ) {
            Log.e(TAG, ex.getMessage());
        } finally {
            if ( cursor!=null ){
                cursor.close();
                cursor = null;
            }
            if (db!=null) {
                db.close();
            }
        }
        return maps;
    }

	/**
	 * <p>Get a ObmBookingVehicleItem object</p>
	 * 
	 * @param bookingVehicleItemId
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
	 * @param bookingVehicleItemId
	 * @return
	 */
	public boolean deleteByBookingVehicleItemId(String bookingVehicleItemId) {
		return super.delete(COLUMN_BOOKING_VEHICLE_ITEM_ID + " = ?", new String[]{bookingVehicleItemId});
	}

    String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
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
		ObmBookingVehicleItem.setBookingNumber(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_NUMBER)));
        ObmBookingVehicleItem.setPickupDate(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_PICKUP_DATE))));
        ObmBookingVehicleItem.setPickupTime(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_TIME)));
        ObmBookingVehicleItem.setPickupDateTime(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_PICKUP_DATE_TIME))));
		ObmBookingVehicleItem.setBookingService(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_SERVICE)));
        ObmBookingVehicleItem.setBookingServiceCd(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_SERVICE_CD)));
        ObmBookingVehicleItem.setBookingHours(cursor.getFloat(cursor.getColumnIndex(COLUMN_BOOKING_HOURS)));
		ObmBookingVehicleItem.setFlightNumber(cursor.getString(cursor.getColumnIndex(COLUMN_FLIGHT_NUMBER)));
		ObmBookingVehicleItem.setPickupAddress(cursor.getString(cursor.getColumnIndex(COLUMN_PICKUP_ADDRESS)));
		ObmBookingVehicleItem.setDestination(cursor.getString(cursor.getColumnIndex(COLUMN_DESTINATION)));
        ObmBookingVehicleItem.setStop1Address(cursor.getString(cursor.getColumnIndex(COLUMN_STOP1_ADDRESS)));
        ObmBookingVehicleItem.setStop2Address(cursor.getString(cursor.getColumnIndex(COLUMN_STOP2_ADDRESS)));
        ObmBookingVehicleItem.setRemark(cursor.getString(cursor.getColumnIndex(COLUMN_REMARK)));

        ObmBookingVehicleItem.setVehicle(cursor.getString(cursor.getColumnIndex(COLUMN_VEHICLE)));
        ObmBookingVehicleItem.setPrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_PRICE)));
        ObmBookingVehicleItem.setPaymentStatus(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_STATUS)));
        ObmBookingVehicleItem.setPaymentMode(cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_MODE)));
        ObmBookingVehicleItem.setBookingStatus(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_STATUS)));

        ObmBookingVehicleItem.setBookingUserFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_FIRST_NAME)));
        ObmBookingVehicleItem.setBookingUserLastName(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_LAST_NAME)));
        ObmBookingVehicleItem.setBookingUserGender(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_GENDER)));
        ObmBookingVehicleItem.setBookingUserMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_MOBILE_NUMBER)));
        ObmBookingVehicleItem.setBookingUserEmail(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_USER_EMAIL)));

		ObmBookingVehicleItem.setLeadPassengerFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_FIRST_NAME)));
		ObmBookingVehicleItem.setLeadPassengerLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_LAST_NAME)));
        ObmBookingVehicleItem.setLeadPassengerGender(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_GENDER)));
		ObmBookingVehicleItem.setLeadPassengerMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_MOBILE_NUMBER)));
        ObmBookingVehicleItem.setLeadPassengerEmail(cursor.getString(cursor.getColumnIndex(COLUMN_LEAD_PASSENGER_EMAIL)));
        ObmBookingVehicleItem.setNumberOfPassenger(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER_OF_PASSENGER)));

		ObmBookingVehicleItem.setDriverUserName(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_USER_NAME)));
        ObmBookingVehicleItem.setDriverLoginUserId(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_LOGIN_USER_ID)));
		ObmBookingVehicleItem.setDriverMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_MOBILE_NUMBER)));
		ObmBookingVehicleItem.setDriverVehicle(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_VEHICLE)));
        ObmBookingVehicleItem.setDriverClaimCurrency(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_CLAIM_CURRENCY)));
        ObmBookingVehicleItem.setDriverClaimPrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_DRIVER_CLAIM_PRICE)));
        ObmBookingVehicleItem.setDriverAction(cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_ACTION)));

        ObmBookingVehicleItem.setAssignDriverUserId(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_USER_ID)));
        ObmBookingVehicleItem.setAssignDriverUserName(cursor.getString(cursor.getColumnIndex(COLUMN_ASSIGN_DRIVER_USER_NAME)));

        ObmBookingVehicleItem.setHistoryDriverUserIds(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORTY_DRIVER_USER_IDS)));
        ObmBookingVehicleItem.setBroadcastTag(cursor.getString(cursor.getColumnIndex(COLUMN_BROADCAST_TAG)));

        ObmBookingVehicleItem.setCreateDateTime(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATE_DATE_TIME))));
        ObmBookingVehicleItem.setModifyTimestamp(new Timestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_MODIFY_TIMESTAMP))));
        ObmBookingVehicleItem.setDataState(cursor.getString(cursor.getColumnIndex(COLUMN_DATA_STATE)));
	}
	
	/**
	 * <p>Insert or update column's value base on bookingVehicleItemId</p>
	 * 
	 * @param bookingVehicleItemId
	 * @param columnName
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
