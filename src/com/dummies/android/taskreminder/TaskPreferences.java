package com.dummies.android.taskreminder;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;

/**
 * Displays preferences
 *
 */
public class TaskPreferences extends PreferenceActivity {
	
	/**
	 * sets up the preferences from xml
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.task_preferences);

		// Set the time default to a numeric number only
		EditTextPreference timeDefault = (EditTextPreference) findPreference(getString(R.string.pref_default_time_from_now_key));
		//digits key listener 
		timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance()); 
	}
}
