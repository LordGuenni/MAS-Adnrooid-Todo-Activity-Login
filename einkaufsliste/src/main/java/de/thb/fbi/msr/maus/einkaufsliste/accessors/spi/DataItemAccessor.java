package de.thb.fbi.msr.maus.einkaufsliste.accessors.spi;

import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;


/**
 * Interface for handling a single data item, used by ItemDetailsActivity
 * 
 * @author Joern Kreutel
 *
 */
public interface DataItemAccessor {

	public TodoItem readItem();
	
	public void writeItem();
	
	public boolean hasItem(); 
	
	public void createItem();
	
	public void deleteItem();
	
}
