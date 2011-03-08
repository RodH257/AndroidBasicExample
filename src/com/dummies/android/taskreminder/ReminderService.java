package com.dummies.android.taskreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

/***
 * The service that actually does the reminders
 * implements the base class that handles the CPU work
 *
 */
public class ReminderService extends WakeReminderIntentService {

	public ReminderService() {
		super("ReminderService");
			}

	/***
	 * Does the reminder work 
	 */
	@Override
	void doReminderWork(Intent intent) {
		
		Log.d("ReminderService", "Doing work.");
		
		//get the ID from the intent passed to thsi class
		Long rowId = intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
		 
		//gets the notification manager
		NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
						
		//gets an intent for the notification to fire off 
		Intent notificationIntent = new Intent(this, ReminderEditActivity.class); 
		notificationIntent.putExtra(RemindersDbAdapter.KEY_ROWID, rowId); 
		
		PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT); 
		
		//constructs a new intent 
		Notification note=new Notification(android.R.drawable.stat_sys_warning, getString(R.string.notify_new_task_message), System.currentTimeMillis());
		//sets the nexpanded notification
		//could be custom layout but simply providing stock notification view
		note.setLatestEventInfo(this, getString(R.string.notify_new_task_title), getString(R.string.notify_new_task_message), pi);
		
		//sets it to play sound if it is on
		note.defaults |= Notification.DEFAULT_SOUND; 
		note.flags |= Notification.FLAG_AUTO_CANCEL; 
		
		// cast to integer for database compatibilituy
		int id = (int)((long)rowId);
		
		//sends the notification to the status bar 
		mgr.notify(id, note); 
				
	}
}
