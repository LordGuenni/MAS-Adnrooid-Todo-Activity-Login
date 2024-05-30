package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.DataItemListViews;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

/**
 * Read out data items from an SQLite database
 * 
 * @author Joern Kreutel
 * @author Martin Schafföner
 * 
 */
public class SQLiteDataItemListAccessor extends
		AbstractActivityDataAccessor implements DataItemListAccessor {

	/**
	 * the logger
	 */
	protected static final String logger = SQLiteDataItemListAccessor.class
			.getName();

	/**
	 * the list of items
	 */
	List<TodoItem> items = new ArrayList<>();

	/**
	 * the adapter operating on the list
	 */
	ArrayAdapter<TodoItem> adapter;

	private SQLiteDBHelper mHelper;

	@Override
	public void addItem(TodoItem item) {
		mHelper.addItemToDb(item);

		/**
		 * we then add the item to the list and notify the adapter
		 */
		this.adapter.add(item);

		// the following two lines have the same effect provided
		// setNotifyOnChange has been set to true on the adapter (which is the
		// case here)
		// this.items.add(item);
		// this.adapter.notifyDataSetChanged();
	}


	@Override
	public ListAdapter getAdapter() {

		mHelper = new SQLiteDBHelper(getActivity());
		mHelper.prepareSQLiteDatabase();

		readOutItemsFromDatabase();

		// create the adapter, overriding the getView method for accessing the
		// name field of the item
		this.adapter = DataItemListViews.createDataItemArrayAdapter(getActivity(),items);
		this.adapter.setNotifyOnChange(true);

		return this.adapter;
	}

	private void readOutItemsFromDatabase() {
		Cursor c = mHelper.getCursor();

		Log.i(logger, "getAdapter(): got a cursor: " + c);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			// create a new item and add it to the list
			this.items.add(mHelper.createItemFromCursor(c));
			c.moveToNext();
		}

		Log.i(logger, "readOutItemsFromDatabase(): read out items: " + this.items);
	}


	@Override
	public void updateItem(TodoItem item) {
		mHelper.updateItemInDb(item);

		// then update the item and notify the adapter
		lookupItem(item).updateFrom(item);
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void deleteItem(TodoItem item) {
		mHelper.removeItemFromDb(item);

		// then we remove the item and notify the adapter
		this.adapter.remove(lookupItem(item));
		
		// this could be done instead:
		// this.items.remove(lookupItem(item));
		// this.adapter.notifyDataSetChanged();
	}


	/**
	 * get the item from the list, checking identity of ids (as the argument
	 * value may have been serialised/deserialised we cannot check for identity
	 * of objects)
	 */
	private TodoItem lookupItem(TodoItem item) {
		for (TodoItem current : this.items) {
			if (current.getId() == item.getId()) {
				return current;
			}
		}
		return null;
	}


	/**
	 * get the items
	 */
	protected List<TodoItem> getItems() {
		return this.items;
	}

	@Override
	public TodoItem getSelectedItem(int itemPosition, long itemId) {
		return adapter.getItem(itemPosition);
	}

	@Override
	public void close() {
		mHelper.close();
	}

}
