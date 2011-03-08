package com.dummies.android.taskreminder;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

/**
 * Handles alarm when it goes off
 * 
 * receives a broadcasted intent 
 *
 */
public class OnAlarmReceiver extends BroadcastReceiver {
	private static final String TAG = ComponentInfo.class.getCanonicalName(); 
	
	/**
	 * entry point for the alarm set
	 */
	@Override	
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received wake up from alarm manager.");
		
		long rowid = intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
		
		//tell the service to keep the CPU a live 
		WakeReminderIntentService.acquireStaticLock(context);
		
		//create a new intent to start the ReminderService
		Intent i = new Intent(context, ReminderService.class);
		//give it the ID
		i.putExtra(RemindersDbAdapter.KEY_ROWID, rowid);  
		context.startService(i);
		 
	}
}
