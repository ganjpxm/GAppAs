/**
 * ConnectionManager.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import java.util.ArrayList;

import android.util.Log;

/**
 * <p>Connection Manager</p>
 *
 * @author GanJianping
 * @since 1.0.0
 */
public class ConnectionManager {

	public static final int MAX_CONNECTIONS = 2;

	private ArrayList<Runnable> actives = new ArrayList<Runnable>();
	private ArrayList<Runnable> queues = new ArrayList<Runnable>();

	private static ConnectionManager instance;

	/**
	 * <p>Get ConnectionManager instance</p>
	 * @return
	 */
	public static ConnectionManager getInstance() {
		if (instance == null)
			instance = new ConnectionManager();
		return instance;
	}

	/**
	 * <p>Push runnable</p>
	 * 
	 * @param runnable
	 */
	public void push(Runnable runnable) {
		queues.add(runnable);
		if (actives.size() < MAX_CONNECTIONS)
			startNext();
	}

	/**
	 * <p>Start next runnable</p>
	 */
	private void startNext() {
		if (!queues.isEmpty()) {
			Runnable next = queues.get(0);
			queues.remove(0);
			actives.add(next);
			Log.i(getClass().toString(), "ConnectionManager.startNext()::Active-size=" + actives.size() + "Queue-size=" + queues.size());
			try {
				Thread thread = new Thread(next);
				thread.start();
			} catch (Exception e) {
				Log.w(getClass().toString(), "ConnectionManager.startNext()::Exception..." + e.getMessage() + "..." + e.toString());
			}
		} else {
			Log.i(getClass().toString(), "ConnectionManager.startNext()::Queue is EMPTY! Active-size=" + actives.size());
		}
	}

	/**
	 * <p>Did complete</p>
	 * 
	 * @param runnable
	 */
	public void didComplete(Runnable runnable) {
		actives.remove(runnable);
		startNext();
	}

}
