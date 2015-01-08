/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onetransport.obs.common.gcm;

import java.util.List;

import org.ganjp.glib.core.ActivityStack;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import com.onetransport.obs.ObsBottomTabFragmentActivity;
import com.onetransport.obs.common.other.ObsUtil;
import com.onetransport.obs.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
//    private ActivityManager activityManager; 
//    private String packageName;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";
    protected String bookingVehicleItemId = "";
    protected String notification = "";
    protected String acceptResult = "";

    @Override
    protected void onHandleIntent(Intent intent) {
    	try {
    		
	        Bundle extras = intent.getExtras();
	        
	        bookingVehicleItemId = extras.getString("bookingVehicleItemId");
	        notification = extras.getString("title");
    		
	        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	        // The getMessageType() intent parameter must be the intent you received in your BroadcastReceiver.
	        String messageType = gcm.getMessageType(intent);
	        
	        Boolean isRun = false;
//	        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE); 
//	        String packageName = this.getPackageName();
//	        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); 
//	        if (appProcesses != null) {
//	           for (RunningAppProcessInfo appProcess : appProcesses) { 
//	            // The name of the process that this object is associated with. 
//	            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) { 
//	          	  isRun = true; 
//	          	  break;
//	            } 
//	          }
//	        }
	        if (ActivityStack.getAcitveActivity()!=null) {
	        	isRun = true;
	        }
	        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
	        	//Bundle[{from=1083654704210, title=30/06/2014 20:25 From Changi Airport Singapore Singapore to Boon Keng Road Singapore, bookingVehicleItemId=8a3080d5457d005a01457f1aeec31aa4, android.support.content.wakelockid=1, collapse_key=do_not_collapse}]
	            if (StringUtil.isNotEmpty(bookingVehicleItemId)) {
	            	if (isRun && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
		                mHandler.obtainMessage(0).sendToTarget();
		        	} else {
			            /*
			             * Filter messages based on message type. Since it is likely that GCM will be
			             * extended in the future with new message types, just ignore any message types you're
			             * not interested in, or that you don't recognize.
			             */
			            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			                sendNotification("Send error: " + extras.toString());
			            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			                sendNotification("Deleted messages on server: " + extras.toString());
			            // If it's a regular GCM message, do some work.
			            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			                // Post notification of received message.
			                sendNotification(extras.toString());
			                Log.i(TAG, "Received: " + extras.toString());
			            }
		        	}
	            }
	        }
	        // Release the wake lock provided by the WakefulBroadcastReceiver.
	        GcmBroadcastReceiver.completeWakefulIntent(intent);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

    private Handler mHandler = new Handler() {  
    	public void handleMessage (Message msg) {
    		if (StringUtil.isNotEmpty(notification)) {
	    		final Activity activity = ActivityStack.getAcitveActivity();
	    		DialogUtil.showAlertDialog(activity, android.R.drawable.ic_dialog_alert, "", notification, new DialogInterface.OnClickListener() {
					@Override
				    public void onClick(DialogInterface dialog, int which) {
	//			        if (which==-1) mMessageTv.setText("ignore");
						if (which==-2) {//Accept
							new Thread(new Runnable() {
					    		public void run() {
					    			try {
					    				acceptResult = ObsUtil.acceptBooking(bookingVehicleItemId);
					    				mHandlerDialog.obtainMessage(0).sendToTarget();
					    			} catch (Exception e) {
					    				e.printStackTrace();
					    				mHandlerDialog.obtainMessage(1).sendToTarget();
					    			}
					    		}
					    	}).start();
				    	}
				    }
		       }, new String[]{activity.getString(R.string.ignore), activity.getString(R.string.accept)});
    		}
    	}  
    }; 
    
    private Handler mHandlerDialog = new Handler() {  
    	public void handleMessage (Message msg) { 
    		final Activity activity = ActivityStack.getAcitveActivity();
    		if (msg.what==0) {
    			DialogUtil.showAlertDialog(activity, acceptResult);
    		} else if (msg.what==1) {
    			DialogUtil.showAlertDialog(activity, "Accept Fail");
    		}
    	}  
    }; 
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
       
        Intent notificationIntent = new Intent(this, ObsBottomTabFragmentActivity.class);
        notificationIntent.putExtra("bookingVehicleItemId", bookingVehicleItemId); 
        notificationIntent.putExtra("content", notification); 
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pushnotification);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
        		.setContentTitle("Driver App").setStyle(new NotificationCompat.BigTextStyle().bigText(notification)).setContentText(notification).setSound(sound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
//    public boolean isAppOnForeground() { 
//        // Returns a list of application processes that are running on the device 
//        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); 
//        if (appProcesses == null) return false; 
//        
//        for (RunningAppProcessInfo appProcess : appProcesses) { 
//            // The name of the process that this object is associated with. 
//            if (appProcess.processName.equals(packageName) 
//                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) { 
//                return true; 
//            } 
//        }
//        
//        return false; 
//    } 
}
