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

package sg.lt.obs.common.gcm;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.StringUtil;

import sg.lt.obs.BookingVehicleAlarmListActivity;
import sg.lt.obs.R;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.other.ObsUtil;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
    NotificationCompat.Builder mBuilder;
//    private ActivityManager activityManager; 
//    private String packageName;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";
    protected String mBookingVehicleItemId = "";
    protected String mNotificationContent = "";
    protected String mAcceptResult = "";

    @Override
    protected void onHandleIntent(Intent intent) {
    	try {
	        Bundle extras = intent.getExtras();
	        
	        mBookingVehicleItemId = extras.getString(ObsConst.KEY_BOOKING_VEHICLE_ITEM_ID);
	        mNotificationContent = extras.getString("title");
    		
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
	        	//Bundle[{from=1083654704210, title=30/06/2014 20:25 From Changi Airport Singapore Singapore to Boon Keng Road Singapore, mBookingVehicleItemId=8a3080d5457d005a01457f1aeec31aa4, android.support.content.wakelockid=1, collapse_key=do_not_collapse}]
	            if (StringUtil.isNotEmpty(mBookingVehicleItemId)) {
//	            	if (isRun && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//		                mHandler.obtainMessage(0).sendToTarget();
//		        	} else {
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
			                // Post mNotificationContent of received message.
			                sendNotification(extras.toString());
			                Log.i(TAG, "Received: " + extras.toString());
			            }
//		        	}
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
    		if (StringUtil.isNotEmpty(mNotificationContent)) {
	    		//final Activity activity = ActivityStack.getAcitveActivity();
    		}
    	}  
    }; 
    
    private Handler mHandlerDialog = new Handler() {  
    	public void handleMessage (Message msg) { 
    		final Activity activity = ActivityStack.getAcitveActivity();
    		if (msg.what==0) {
    			DialogUtil.showAlertDialog(activity, mAcceptResult);
    		} else if (msg.what==1) {
    			DialogUtil.showAlertDialog(activity, "Accept Fail");
    		}
    	}  
    }; 
    // Put the message into a mNotificationContent and post it.
    // This is just one simple example of what you might choose to do with a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
       
        Intent notificationIntent = new Intent(this, BookingVehicleAlarmListActivity.class);
        notificationIntent.putExtra("mBookingVehicleItemId", mBookingVehicleItemId);
        notificationIntent.putExtra("content", mNotificationContent);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pushnotification);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
        		.setContentTitle("Limousine Transport").setStyle(new NotificationCompat.BigTextStyle().bigText(mNotificationContent)).setContentText(mNotificationContent).setSound(sound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        new Thread(new Runnable() {
            public void run() {
                try {
                    ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
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
