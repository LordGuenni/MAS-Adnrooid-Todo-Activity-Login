package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.intent;

import android.content.Intent;
import de.thb.fbi.msr.maus.einkaufsliste.ItemListActivity;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

/**
 * Access a single item provided by the ItemListActivity in the intent used for
 * calling ItemDetailsActivity
 * 
 * @author Joern Kreutel
 * 
 */
public class IntentDataItemAccessor extends AbstractActivityDataAccessor
		implements DataItemAccessor {

	// the item
	private TodoItem item;

	@Override
	public TodoItem readItem() {
		if (this.item == null) {
			this.item = (TodoItem) getActivity().getIntent()
					.getSerializableExtra(ItemListActivity.ARG_ITEM_OBJECT);
		}

		return this.item;
	}

	@Override
	public void writeItem() {
		// and return to the calling activity

		Intent returnIntent = new Intent();

		// set the item
		returnIntent.putExtra(ItemListActivity.ARG_ITEM_OBJECT, this.item);

		// set the result code
		getActivity().setResult(ItemListActivity.RESPONSE_ITEM_EDITED,
				returnIntent);
	}

	public boolean hasItem() {
		return readItem() != null;
	}

	public void createItem() {
		this.item = new TodoItem("", "", false, false, 0);
	}

	@Override
	public void deleteItem() {
		// and return to the calling activity
		Intent returnIntent = new Intent();

		// set the item
		returnIntent.putExtra(ItemListActivity.ARG_ITEM_OBJECT, this.item);

		// set the result code
		getActivity().setResult(ItemListActivity.RESPONSE_ITEM_DELETED,
				returnIntent);
	}

}
