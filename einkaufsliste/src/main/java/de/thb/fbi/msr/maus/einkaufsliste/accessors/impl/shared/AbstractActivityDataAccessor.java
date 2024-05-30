package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;

/**
 * allows access to the activity object for its subclasses
 * 
 * @author Joern Kreutel
 *
 */
public abstract class AbstractActivityDataAccessor {

	private AppCompatActivity activity;

	protected AppCompatActivity getActivity() {
		return this.activity;
	}

	public void setActivity(AppCompatActivity activity) {
		this.activity = activity;
	}
	
}
