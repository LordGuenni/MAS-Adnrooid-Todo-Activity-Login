package de.thb.fbi.msr.maus.einkaufsliste.accessors.spi;

import android.widget.ListAdapter;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;


/**
 * Interface for dealing with a list of data items, used by ItemListActivity
 * 
 * @author Joern Kreutel
 * 
 */
public interface DataItemListAccessor {

	/**
	 * add an item to the list
	 * 
	 * @param item
	 */
	public void addItem(TodoItem item);

	/**
	 * get an adapter for the list
	 * @return
	 */
	public ListAdapter getAdapter();

	/**
	 * update an existing item
	 * 
	 * @param item
	 */
	public void updateItem(TodoItem item);

	/**
	 * delete an item
	 * 
	 * @param item
	 */
	public void deleteItem(TodoItem item);

	/**
	 * determine the item selected by the user given either the position in the
	 * list or the item id
	 * 
	 * @param itemPosition
	 * @param itemId
	 * @return
	 */
	public TodoItem getSelectedItem(int itemPosition, long itemId);

	/**
	 * end processing the list of items
	 */
	public void close();

}
