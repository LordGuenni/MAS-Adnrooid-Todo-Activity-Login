package de.thb.fbi.msr.maus.einkaufsliste;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;
import de.thb.fbi.msr.maus.einkaufsliste.model.spi.DataAccessResourceCRUDHandler;

/**
 * a local application class providing centralised functionality, in particular
 * the CRUD functionality that uses the remote data storage on the server
 * 
 * @author Joern Kreutel
 */
public class DataAccessApplication extends Application implements
		DataAccessResourceCRUDHandler {

	@Override
	public TodoItem readDataItem(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TodoItem> readAllDataItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDataItem(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public TodoItem createDataItem(TodoItem item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TodoItem updateDataItem(TodoItem item) {
		// TODO Auto-generated method stub
		return null;
	}

	public void reportError(Activity activity, String error) {
		Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
	}

}
