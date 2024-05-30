package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.resources;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import de.thb.fbi.msr.maus.einkaufsliste.R;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.DataItemListViews;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

/**
 * Read out a list of items from a (static) resource file, which does not allow
 * for write access
 * 
 * @author Joern Kreutel
 * 
 */
public class ResourcesDataItemListAccessor extends
		AbstractActivityDataAccessor implements DataItemListAccessor {

	// list
	private List<TodoItem> itemlist;

	// adapter
	private ArrayAdapter<TodoItem> adapter;

	// logger
	protected static final String logger = ResourcesDataItemListAccessor.class
			.getName();

	@Override
	public void addItem(TodoItem item) {
		this.itemlist.add(item);
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public ListAdapter getAdapter() {

		// the options
		String[] items = getActivity().getResources().getStringArray(
				R.array.item_list);
		// create a list
		itemlist = new ArrayList<>();
		for (String item : items) {
			itemlist.add(new TodoItem(-1,"", "", false,false,0));
		}

		// create the adapter from the list 
		adapter = DataItemListViews.createDataItemArrayAdapter(getActivity(), itemlist);
		
		return adapter;
	}

	@Override
	public void updateItem(TodoItem item) {
		Log.i(logger, "updating item: " + item);
		lookupItem(item).updateFrom(item);
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void deleteItem(TodoItem item) {
		this.itemlist.remove(lookupItem(item));
		this.adapter.notifyDataSetChanged();
	}

	/**
	 * get the item from the list, checking identity of ids (as the argument
	 * value may have been serialised/deserialised we cannot check for identity
	 * of objects)
	 */
	private TodoItem lookupItem(TodoItem item) {
		for (TodoItem current : this.itemlist) {
			if (current.getId() == item.getId()) {
				return current;
			}
		}
		return null;
	}

	@Override
	public TodoItem getSelectedItem(int itemPosition, long itemId) {
		return adapter.getItem(itemPosition);
	}

	@Override
	public void close() {
		// nothing to do
	}

}
