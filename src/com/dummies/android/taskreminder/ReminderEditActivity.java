package com.dummies.android.taskreminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class ReminderEditActivity extends Activity {

	private Button mDateButton;
	private Button mTimeButton;
	private RemindersDbAdapter mDbHelper;
	private EditText mTitleText;
	private Button mConfirmButton;
	private EditText mBodyText;
	private Calendar mCalendar;
	private Long mRowId;
	
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG = 1;
	
	//format for date/times to be shown as string
	private static final String DATE_FORMAT = "yyy-MM-dd";
	private static final String TIME_FORMAT = "kk:mm";
	//database format
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
		
	/**
	 * When the Activity is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//get the database adapter
		mDbHelper = new RemindersDbAdapter(this);
		setContentView(R.layout.reminder_edit);
		
		// get a calendar object
		mCalendar = Calendar.getInstance();
		//get the various class variables from XML
		mConfirmButton = (Button) findViewById(R.id.confirm);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		mDateButton = (Button) findViewById(R.id.reminder_date);
		mTimeButton = (Button) findViewById(R.id.reminder_time);

		
		mRowId = savedInstanceState != null ? savedInstanceState.getLong(RemindersDbAdapter.KEY_ROWID) : null;
		
		registerButtonListenersAndSetDefaultText();
		
	}

	/**
	 * Sets the mRowId v ariable based on the extra value supplied in the intent
	 */
	private void setRowIdFromIntent() {
		if (mRowId == null) {
			//getIntent is on the activity base class to retrieve the incoming intent
			//a bundle is a key value pair data structure
			Bundle extras = getIntent().getExtras();            
			mRowId = extras != null ? extras.getLong(RemindersDbAdapter.KEY_ROWID) 
									: null;
			
		}
	}
	
	/**
	 * Closes the database on shut down or pause
	 * (both fire this method) 
	 */
    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close(); 
    }
    
    /**
     * When the activity is resumed the database is opened again
     * row id is set
     * and fields populated
     */
    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open(); 
    	setRowIdFromIntent();
		populateFields();
    }
    
    /**
     * Events for when a dialog is craeted, intercepts it and shows the certain dialog based on
     * constant integer value
     */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_DIALOG:
			return showDatePicker();
		case TIME_PICKER_DIALOG:
			return showTimePicker();
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * Sets up the date picker dialog
	 * @return
	 */
	private DatePickerDialog showDatePicker() {
		DatePickerDialog datePicker =
		// pass this class as the context, as its a nested statement
		// you need the full class name
		new DatePickerDialog(
				ReminderEditActivity.this,
				new DatePickerDialog.OnDateSetListener() {

					// on date set is called when
					// the date picker has set the date
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mCalendar.set(Calendar.YEAR, year);
						mCalendar.set(Calendar.MONTH, monthOfYear);
						mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						updateDateButtonText();
					}
				},
				// gets the default values for the date picker
				// mcalendar will reutrn the previously set values
				// or todays date
				mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		// return datepicker because oncreateDialog requires it to display it on
		// screen
		return datePicker;
	}
	
	/**
	 * sets up the time picker dialog 
	 * @return
	 */
	private Dialog showTimePicker() {

		TimePickerDialog timePicker = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
						mCalendar.set(Calendar.MINUTE, minute);
						updateTimeButtonText();
					}

				
				}, mCalendar.get(Calendar.HOUR_OF_DAY),
				mCalendar.get(Calendar.MINUTE), true);

		return timePicker;
	}
	
	/**
	 * Registers the button listeners and updates the text on date and time buttons 
	 */
	private void registerButtonListenersAndSetDefaultText() {
		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);

			}
		});

		mTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);
			}
		});
		
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveState();
				//sets the result of the activity, which is used from the reminder list activityclass 
				setResult(RESULT_OK);
				//creats a toast to let the user know its been saved
				Toast.makeText(ReminderEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
				finish();
			}
		});
			
		updateDateButtonText();
		updateTimeButtonText();
	}

	
	/***
	 * Populates the fields on the form
	 */
	 private void populateFields()  {
	    	    	
	    	// Only populate the text boxes and change the calendar date
	    	// if the row is not null from the database. 
	        if (mRowId != null) {
	            Cursor reminder = mDbHelper.fetchReminder(mRowId);
	            //set it to clean up the cursor when the activity ends
	            startManagingCursor(reminder);
	            
	            //gets the string from the cursor based on the 
	            //column index of the certain column
	            mTitleText.setText(reminder.getString(
	    	            reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_TITLE)));
	            mBodyText.setText(reminder.getString(
	                    reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_BODY)));
	            
	            // Get the date from the database and format it for our use. 
	            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
	            Date date = null;
				try {
					String dateString = reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_DATE_TIME)); 
					date = dateTimeFormat.parse(dateString);
		            mCalendar.setTime(date); 
				} catch (ParseException e) {
					//prints error to system log 
					Log.e("ReminderEditActivity", e.getMessage(), e); 
				} 
	        } else {
	        	// This is a new task - add defaults from preferences if set. 
	        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
	        	
	        	//gets the keys from strings 
	        	String defaultTitleKey = getString(R.string.pref_task_title_key); 
	        	String defaultTimeKey = getString(R.string.pref_default_time_from_now_key); 
	        	
	        	//retrieve the value from preferences 
	        	String defaultTitle = prefs.getString(defaultTitleKey, null);
	        	String defaultTime = prefs.getString(defaultTimeKey, null); 
	        	
	        	if(defaultTitle != null)
	        		mTitleText.setText(defaultTitle); 
	        	
	        	//adds the set minutes to add to the calendar
	        	//stored as string so need to cast to int 
	        	if(defaultTime != null)
	        		mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
	        	
	        }
	        
	        updateDateButtonText(); 
	        updateTimeButtonText(); 
	        	
	    }

	/**
	 * Updates the time button text
	 */
	private void updateTimeButtonText() {
		// Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT); 
        String timeForButton = timeFormat.format(mCalendar.getTime()); 
        mTimeButton.setText(timeForButton);
	}
	
	

	/**
	 * Updates the text of the current date 
	 */
	private void updateDateButtonText() {
		// formats the date in a local-sensitive manner
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		String dateForButton = dateFormat.format(mCalendar.getTime());
		mDateButton.setText(dateForButton);
	}
	
	/***
	 * Saves the mRowId in instance state 
	 * you can store activity level instances in a bundle here.
	 * This gets used when killign activity and onresume then restores it 
	 * back to how it was. 
	 */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RemindersDbAdapter.KEY_ROWID, mRowId);
    }
	
	/**
	 * Saves the state from the inputs into the sqlite database
	 */
    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT); 
    	String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

        if (mRowId == null) {
        	//must be a new activity
        	long id = mDbHelper.createReminder(title, body, reminderDateTime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
        	//must be updateding an old one 
            mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
        }
       
        new ReminderManager(this).setReminder(mRowId, mCalendar); 
    }
}
