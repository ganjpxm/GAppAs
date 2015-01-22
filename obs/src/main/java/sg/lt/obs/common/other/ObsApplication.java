/**
 * ObsApplication.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.other;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.ganjp.glib.core.base.BaseApplication;

import java.util.HashMap;

import sg.lt.obs.R;
import sg.lt.obs.common.dao.SystemDAO;
import sg.lt.obs.common.dao.ObsDaoFactory;
import sg.lt.obs.common.dao.ObsDaoFactory.DAOType;


/**
 * <p>Base Application</p>
 * 
 * @author GanJianping
 * @since v1.0.0
 */
public class ObsApplication extends BaseApplication {
    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-52110558-2";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public ObsApplication() {
        super();
    }

    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    /**
	 * <p>Get System DAO</p>
	 */
	public static SystemDAO getSystemDAO() {
		return (SystemDAO) (ObsDaoFactory.getInstance().getDAO(DAOType.SYSTEM, ObsApplication.getAppContext()));
	}
}	
