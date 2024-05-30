package de.thb.fbi.msr.maus.einkaufsliste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Provide the main menu for this demo application and allow to select the
 * particular data access way
 * 
 * @author Joern Kreutel
 * 
 */
public class DataAccessActivity extends AppCompatActivity {

	/**
	 * the logger
	 */
	protected static final String logger = DataAccessActivity.class.getName();

	/**
	 * the name of the argument that specifies the actual class to be used for
	 * data access in the activities started from here
	 */
	public static final String ARG_ACCESSOR_CLASS = "accessorClass";

	/**
	 * the subpackage name for accessor
	 */
	private static final String ACCESSORS_SUBPACKAGE = "accessors.impl";


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// set the list view as content view
		setContentView(R.layout.itemlistview);

		/*
		 * access the list view for the options to be displayed
		 */
		ListView listview = findViewById(R.id.list);

		// read out the options
		final String[] menuItems = getResources().getStringArray(R.array.main_menu);

		/*
		 * create an adapter that allows for the view to access the list's
		 * content and that holds information about the visual
		 * representation of the list items
		 */
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuItems);

		// set the adapter on the list view
		listview.setAdapter(adapter);

			// set a listener that reacts to the selection of an element
		listview.setOnItemClickListener((adapterView, selectedView, itemPosition, arg3) -> {
					processItemClick(adapterView, itemPosition);
				}
		);

	}

	private void processItemClick(AdapterView<?> adapterView, int itemPosition) {
		String selectedItem = (String) adapterView.getAdapter().getItem(itemPosition);

		Log.i(this.getClass().getName(), "got item selected: " + selectedItem);

		handleSelectedItem(selectedItem);
	}

	protected void handleSelectedItem(String selectedItem) {
		/*
		 * depending on the selected item, we create an activity and pass it the
		 * accessor class to be used. The class name will be created given the
		 * selected item
		 * 
		 * that quite lengthy expressions does the following:
		 * 
		 * 1) determine the position of the selected item in the main_menu array
		 * resource
		 * 
		 * 2) read out that position from the main_menu_activities and
		 * main_menu_accessors resources, respectively
		 */
		try {
			final String[] accessorDisplayNames = getResources().getStringArray(R.array.main_menu);
			final List<String> accessorsDisplayList = Arrays.asList(accessorDisplayNames);
			final int indexOfSelectedAccessor = accessorsDisplayList.indexOf(selectedItem);
			final Class<?> activityClass = Class.forName(this.getClass().getPackage().getName()
					+ "."
					+ getResources().getStringArray(R.array.main_menu_activities)[indexOfSelectedAccessor]);
			Intent intent = new Intent(DataAccessActivity.this, activityClass);
			final String accessorClass = DataAccessActivity.class.getPackage().getName()
					+ "." + ACCESSORS_SUBPACKAGE + "."
					+ getResources().getStringArray(R.array.main_menu_accessors)[indexOfSelectedAccessor];
			intent.putExtra(ARG_ACCESSOR_CLASS,	accessorClass);

			/*
			 * start the activity
			 */
			startActivity(intent);
		} catch (Exception e) {
			String err = "got exception trying to handle selected item "
					+ selectedItem + ": " + e;
			Log.e(logger, err, e);
			((DataAccessApplication) getApplication())
			.reportError(this, err);
		}
	}

}