package de.thb.fbi.msr.maus.einkaufsliste;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room.RoomDataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room.Converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Show the details of an item
 *
 * @author Joern Kreutel
 *
 */
public class ItemDetailsActivity extends AppCompatActivity {

	/**
	 * the logger
	 */
	protected static final String logger = ItemDetailsActivity.class.getName();

	/**
	 * the accessor for dealing with the item to be displayed and edited
	 */
	private DataItemAccessor accessor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemview);

		try {
			// obtain the ui elements
			final EditText itemName = findViewById(R.id.item_name);
			final EditText itemDescription = findViewById(R.id.item_description);
			Button saveButton = findViewById(R.id.saveButton);
			Button deleteButton = findViewById(R.id.deleteButton);

			// Checkbox and Date
			CheckBox favoriteCheckbox = findViewById(R.id.item_favorite);
			CheckBox doneCheckbox = findViewById(R.id.item_done);
			EditText dueDateInput = findViewById(R.id.item_due_date);

			Class<?> accessorClass = Class.forName(getIntent().getStringExtra(
					DataAccessActivity.ARG_ACCESSOR_CLASS));

			this.accessor = (DataItemAccessor) accessorClass.newInstance();
			// if we have an ActivityDataAccessor, we pass ourselves
			if (accessor instanceof AbstractActivityDataAccessor) {
				((AbstractActivityDataAccessor) accessor).setActivity(this);
			}

			// set the title of the activity given the accessor class
			setTitle(accessorClass.getName().substring(
					accessorClass.getName().lastIndexOf(".") + 1));

			// if we do not have an item, we assume we need to create a new one
			if (accessor.hasItem()) {
				// set name and description
				itemName.setText(accessor.readItem().getName());
				itemDescription.setText(accessor.readItem().getDescription());
				favoriteCheckbox.setChecked(accessor.readItem().isFavorite());
				doneCheckbox.setChecked(accessor.readItem().isFinished());
				dueDateInput.setText(fromUnixTimeToDate((accessor.readItem().getDueDate())));
			} else {
				accessor.createItem();
			}
			// we only set a listener on the ok button that will collect the
			// edited fields and set the values on the item
			saveButton.setOnClickListener(( v) -> {
					processItemSave(accessor, itemName, itemDescription, favoriteCheckbox, doneCheckbox, dueDateInput);
				}
			);

			dueDateInput.setOnClickListener(v -> {
				// Get the current date
				final Calendar calendar = Calendar.getInstance();
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH);
				int year = calendar.get(Calendar.YEAR);

				// Create a new instance of DatePickerDialog and return it
				DatePickerDialog datePickerDialog = new DatePickerDialog(this,
						(view, year1, monthOfYear, dayOfMonth) -> {
							// Set the selected date on the EditText
							calendar.set(year1, monthOfYear, dayOfMonth);
							String selectedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
							dueDateInput.setText(selectedDate);
						}, year, month, day);
				datePickerDialog.show();
			});

			// we also set a listener on the delete button
			// we only set a listener on the ok button that will collect the
			// edited fields and set the values on the item
			deleteButton.setOnClickListener((v) -> {
					processItemDelete(accessor);
				}
			);


		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			((DataAccessApplication) getApplication())
			.reportError(this, err);
		}

	}

	/**
	 * save the item and finish
	 * @param accessor
	 * @param name
	 * @param description
	 */
	protected void processItemSave(DataItemAccessor accessor, EditText name,
								   EditText description, CheckBox favorite, CheckBox done, TextView dueDate) {
		// re-set the fields of the item
		accessor.readItem().setName(name.getText().toString());
		accessor.readItem().setDescription(description.getText().toString());
		accessor.readItem().setFavorite(favorite.isChecked());
		accessor.readItem().setFinished(done.isChecked());
		accessor.readItem().setDueDate(fromDatetoLong(dueDate));
		// save the item
		if (accessor instanceof RoomDataItemListAccessor) {
			((RoomDataItemListAccessor) accessor).updateItem(accessor.readItem());
		} else {
			accessor.writeItem();
		}
		// and finish
		finish();
	}

	/**
	 * delete the item and finish
	 * @param accessor
	 */
	protected void processItemDelete(DataItemAccessor accessor) {
		// delete the item
		accessor.deleteItem();

		// and finish
		finish();
	}

	public Long fromDatetoLong(TextView aDateString) {
		String dateString = aDateString.getText().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
		try {
			Date date = sdf.parse(dateString);
			long unixTime = date.getTime() / 1000;
			return unixTime;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String fromUnixTimeToDate(long unixTime) {
		// Convert unix time to milliseconds
		long timestamp = unixTime * 1000L;

		// Create a Date object
		Date date = new Date(timestamp);

		// Format the date into MM/dd/yy format
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
		return sdf.format(date);
	}



}
