package de.thb.fbi.msr.maus.einkaufsliste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.intent.IntentDataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

/**
 * Show a list of items
 * 
 * @author Joern Kreutel
 * @author Martin Schafföner
 *
 */
public class ItemListActivity extends AppCompatActivity {

	// the logger
	protected static final String logger = DataAccessActivity.class.getName();

	/**
	 * the argname with which we will pass the item to the subview
	 */
	public static final String ARG_ITEM_OBJECT = "itemObject";

	/**
	 * the result code that indicates that some item was changed
	 */
	public static final int RESPONSE_ITEM_EDITED = 1;

	/**
	 * the result code that indicates that the item shall be deleted
	 */
	public static final int RESPONSE_ITEM_DELETED = 2;

	/**
	 * the result code that indicates that nothing has been changed
	 */
	public static final int RESPONSE_NOCHANGE = -1;

	/**
	 * the constant for the subview request
	 */
	public static final int REQUEST_ITEM_DETAILS = 2;

	/**
	 * the constant for the new item request
	 */
	public static final int REQUEST_ITEM_CREATION = 1;

	/**
	 * the field for the listview
	 */
	private ListView listview;

	/**
	 * the data accessor for the data items
	 */
	private DataItemListAccessor accessor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlistview);

		// access the listview
		/*
		 * access the list view for the options to be displayed
		 */
		listview = findViewById(R.id.list);

		// the button for adding new items
		final FloatingActionButton newitemButton = findViewById(R.id.newitemButton);

		initAccessor();

		// obtain the adapter from the accessor, passing it the id of the
		// item layout to be used
		final ListAdapter adapter = accessor.getAdapter();

		// set the adapter on the list view
		listview.setAdapter(adapter);

		// set a listener that reacts to the selection of an element
		listview.setOnItemClickListener((adapterView, itemView, itemPosition, itemId) -> {

			processListItemClick(itemPosition, itemId);
		});

		// set the listview as scrollable
		listview.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);

		// set a listener for the newItemButton
		newitemButton.setOnClickListener((v) -> {
			processNewItemRequest();
		});

	}

	private void processListItemClick(int itemPosition, long itemId) {
		Log.i(logger, "onItemClick: position is: " + itemPosition
				+ ", id is: " + itemId);

		TodoItem item = accessor.getSelectedItem(itemPosition, itemId);

		processItemSelection(item);
	}

	private void initAccessor()  {
		try {
			// determine the accessor which shall be used
			Class<?> accessorClass = Class.forName(getIntent().getStringExtra(DataAccessActivity.ARG_ACCESSOR_CLASS));
			accessor = (DataItemListAccessor) accessorClass.newInstance();
			// if we have an ActivityDataAccessor, we pass ourselves
			if (accessor instanceof AbstractActivityDataAccessor) {
				((AbstractActivityDataAccessor) accessor).setActivity(this);
			}

			// set the title of the activity given the accessor class
			setTitle(accessorClass.getName().substring(accessorClass.getName().lastIndexOf(".") + 1));
		} catch (Exception e) {
			Log.e("Got exception: ", String.valueOf(e));
		}

		Log.i(logger, "will use accessor: " + accessor);
	}

	protected void processNewItemRequest() {
		Log.i(logger, "processNewItemRequest()");
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);
		// we only specify the accessor class
		intent.putExtra(DataAccessActivity.ARG_ACCESSOR_CLASS, IntentDataItemAccessor.class.getName());

		// start the details activity with the intent
		startActivityForResult(intent, REQUEST_ITEM_CREATION);
	}

	private void processItemSelection(TodoItem item) {
		Log.i(logger, "processItemSelection(): " + item);
		// create an intent for opening the details view
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);
		// pass the item to the intent
		intent.putExtra(ARG_ITEM_OBJECT, item);
		// also specify the accessor class
		intent.putExtra(DataAccessActivity.ARG_ACCESSOR_CLASS,
				IntentDataItemAccessor.class.getName());

		// start the details activity with the intent
		startActivityForResult(intent, REQUEST_ITEM_DETAILS);
	}

	/**
	 * process the result of the item details subactivity, which may be the
	 * creation, modification or deletion of an item.
	 * 
	 * NOTE that is not necessary to invalidate the listview if changes are
	 * communicated to the adapter using notifyDataSetChanged()
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(logger, "onActivityResult(): " + data);

		TodoItem item = data != null ? (TodoItem) data
				.getSerializableExtra(ARG_ITEM_OBJECT) : null;

		// check which request we had
		if (requestCode == REQUEST_ITEM_DETAILS) {
			if (resultCode == RESPONSE_ITEM_EDITED) {
				Log.i(logger, "onActivityResult(): updating the edited item");
				this.accessor.updateItem(item);
			} else if (resultCode == RESPONSE_ITEM_DELETED) {
				this.accessor.deleteItem(item);
			}
			// this.listview.invalidate();
		} else if (requestCode == REQUEST_ITEM_CREATION
				&& resultCode == RESPONSE_ITEM_EDITED) {
			Log.i(logger, "onActivityResult(): adding the created item");
			this.accessor.addItem(item);
		}

	}
	
	/**
	 * if we stop, we signal this to the accessor (which is necessary in order to avoid trouble when operating on dbs)
	 */
	@Override
	public void onDestroy() {
		Log.i(logger,"onDestroy(): will signal finalisation to the accessors");
		this.accessor.close();
		
		super.onStop();
	}

}
