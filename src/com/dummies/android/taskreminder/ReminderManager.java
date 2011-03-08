package com.dummies.android.taskreminder;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Interfaces with the android AlarmManager class to schedule reminders
 *
 */
public class ReminderManager {
	private Context mContext; 
	private AlarmManager mAlarmManager;
	
	/**
	 * Instantiate with context
	 */
	public ReminderManager(Context context) {
		mContext = context; 
		//get the alarm manager from getSystemService
		mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	/**
	 * sets the remidner to fire off an alarm
	 * @param taskId
	 * @param when
	 */
	public void setReminder(Long taskId, Calendar when) {
		//intent specifies what happens when alarm goes off, specifies to start OnAlarmReceiver
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        //add the id of the task 
        i.putExtra(RemindersDbAdapter.KEY_ROWID, (long)taskId); 
        
        //cross process communication done by a pending intent 
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, PendingIntent.FLAG_ONE_SHOT); 
        
       
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
	}
}
