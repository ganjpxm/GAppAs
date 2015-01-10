/**
 * DatabaseHelper.java
 * 
 * Created by Gan Jianping on 12/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.dao;

import sg.lt.obs.common.ObsConst;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * <p>Database Helper of SQLite</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	protected int oldVersion;
	protected int newVersion;
	
	/**
	 * <p>Init application's database</p>
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, ObsConst.DATABASE_NAME, null, ObsConst.DATABASE_VERSION);
		this.getDatabase();
	}

    /**
     * <p>Get the database instance</p>
     *
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDatabase() {
        return this.getWritableDatabase();
    }

	/**
	 * <p>Listener for creating database</p>
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (db!=null) {
			Log.d("DatabaseHelper", "onCreate:" + db.getPath());
		}
	}

	/**
	 * <p>Listener for upgrade database</p>
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("DatabaseHelper", "onUpgrade:"+oldVersion+":"+newVersion);
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		onCreate(db);
	}

	/**
	 * <p>Check if this requires an upgrade</p>
	 * 
	 * @return
	 */
	public boolean requireUpgrade() {
		return (this.oldVersion != this.newVersion);
	}

    /**
     * <p>Finalize the databse connection</p>
     */
    @Override
    public void finalize() {
        this.close();
        try {
            super.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
