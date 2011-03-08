package com.dummies.android.taskreminder;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * ListActivity is a different base class, for lists
 * 
 * @author Rodh257
 * 
 */
public class ReminderListActivity extends ListActivity {
	
	private static final int ACTIVITY_CREATE = 0; //request  code  that is returned to OnActivityResult 
	private static final int ACTIVITY_EDIT = 1;
	private RemindersDbAdapter mDbHelper;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_list);
		mDbHelper = new RemindersDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
		
	}

	/**
	 * Fills the database data in the list view
	 */
	private void fillData(){
	       Cursor remindersCursor = mDbHelper.fetchAllReminders();
	       //takes care of the cursors life cycle for you
	       //calls deactiviate on teh cursor when the activity is stopped
	       //calls requery whe nthe activity is restarted
	        startManagingCursor(remindersCursor);
	        
	        
	        // Create an array to specify the fields we want to display in the list (only TITLE)
	        String[] from = new String[]{RemindersDbAdapter.KEY_TITLE};
	        
	        // and an array of the fields we want to bind those fields to (in this case just text1)
	        int[] to = new int[]{R.id.text1};
	        
	        // Now create a simple cursor adapter and set it to display
	        //simplecursoradapter 
	        SimpleCursorAdapter reminders = 
	        	    new SimpleCursorAdapter(this, R.layout.reminder_row, remindersCursor, from, to);
	        setListAdapter(reminders);
			
	}
	
	/**
	 * When an item in the list is tapped 
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	      super.onListItemClick(l, v, position, id);
	        Intent i = new Intent(this, ReminderEditActivity.class);
	        i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
	        startActivityForResult(i, ACTIVITY_EDIT); 
	}

	

    
	/**
	 * When there is al ong press on a list view item it will createa context
	 * menu
	 * 
	 * @param v
	 *            View that was long pressed on
	 * @param menu
	 *            the menu being constructed
	 * @param menuInfo
	 *            extra info
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu_item_longpress, menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu, menu);
		return true;
	}

	/**
	 * When a menu item is selected
	 * 
	 * @param featureId
	 *            - the panel that the menu is located on
	 * @param item
	 *            - the menu item taht was clicked
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_insert:
			createReminder();
			return true;
        case R.id.menu_settings: 
        	Intent i = new Intent(this, TaskPreferences.class); 
        	startActivity(i); 
            return true;
        }
		return super.onMenuItemSelected(featureId, item);
	}

	

	/**
	 * Creates a new intent that starts the ReminderEditActivity 
	 */
	private void createReminder() {
		Intent i = new Intent(this, ReminderEditActivity.class);
		
		//start activity for result is used when you want to know when its completed
		// we will be addind the new item to the list 
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	/**
	 * Called when the edit activity is finished 
	 * @param requestCode - the request code supplied in createReminder
	 * @param resultCode - the result returned by the child activity. Determines wether itw as complete,d cancelled or terminated
	 * @param intent - an intent that the child activity craetes to return result data 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);
		//reload the list  to include new activities
        fillData();
	}
	
	/**
	 * Called when a context item is selected
	 * @param item - the item that was selected in the menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item){
		switch(item.getItemId()) {
		//matches to the ID from the list_menu_item_longpress.xml file 
    	case R.id.menu_delete:
    		//adapter context menu info gets some info about wht was solected
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        mDbHelper.deleteReminder(info.id);
	        fillData();
	        return true;
		}
		return super.onContextItemSelected(item);

	}

}