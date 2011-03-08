package com.dummies.android.taskreminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Base class for the ReminderService class Handles CPU lock
 * 
 */
public abstract class WakeReminderIntentService extends IntentService {

	//specfies method to be run in child classes
	abstract void doReminderWork(Intent intent);

	//tag name of lock for CPU - used for debugging
	public static final String LOCK_NAME_STATIC = "com.dummies.android.taskreminder.Static";
	
	//wake lock variable 
	private static PowerManager.WakeLock lockStatic = null;

	/*
	 * gets the lock and activates it 
	 */
	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}

	/**
	 * Gets the power manager.wakelock 
	 * @param context
	 * @return
	 */
	synchronized private static PowerManager.WakeLock getLock(Context context) {
		//checks if you already have it 
		if (lockStatic == null) {
			//gets the power manager service
			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			//creates a new partial wake lock
			//partial means you don't want to turn on the screen
			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					LOCK_NAME_STATIC);
			
			//tells power manager reference is counted
			lockStatic.setReferenceCounted(true);
		}
		return (lockStatic);
	}

	/**
	 * Constructor with name of child instance that craeted it for debugging purposes 
	 * @param name
	 */
	public WakeReminderIntentService(String name) {
		super(name);
	}

	/**
	 * when the service is called it calls the doReminderWork method 
	 * and then releases the wake lock so that the CPU isn't stuck in an on site 
	 * and drains the memory 
	 */
	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			doReminderWork(intent);
		} finally {
			getLock(this).release();
		}
	}
}
