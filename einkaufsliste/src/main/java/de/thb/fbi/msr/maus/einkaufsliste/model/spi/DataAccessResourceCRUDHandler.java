package de.thb.fbi.msr.maus.einkaufsliste.model.spi;

import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

import java.util.ArrayList;

/**
 * CRUD interface for handling data items
 * 
 * @author Joern Kreutel
 *
 */
public interface DataAccessResourceCRUDHandler {
	
	public TodoItem readDataItem(Long id);

	public ArrayList<TodoItem> readAllDataItem();

	public void deleteDataItem(Long id);

	public TodoItem createDataItem(TodoItem item);
	
	public TodoItem updateDataItem(TodoItem item);
}
