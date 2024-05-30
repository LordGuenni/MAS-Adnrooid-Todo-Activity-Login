package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

/**
 * Using SharedPreferences to read and write a single item
 *
 * @author Joern Kreutel
 */
public class PreferencesDataItemAccessor extends AbstractActivityDataAccessor implements DataItemAccessor {

    /**
     * the logger
     */
    protected static final String logger = PreferencesDataItemAccessor.class.getName();

    /**
     * the shared preferences id
     */
    public static final String PREFERENCES_ID = "SharedPreferencesDataItem";

    /**
     * the item id preference
     */
    private static final String PREF_ITEM_ID = "dataItemId";

    /**
     * the item name preference
     */
    private static final String PREF_ITEM_NAME = "dataItemName";

    /**
     * the item description preference
     */
    private static final String PREF_ITEM_DESC = "dataItemDescription";

    /**
     * the item type preference
     */
    private static final String PREF_ITEM_TYPE = "dataItemType";

    private static final Boolean PREF_ITEM_FINISHED = false;
    private static final Boolean PREF_ITEM_FAVORITE = false;
    private static final Long PREF_ITEM_DUEDATE = 0l;

    /**
     * the item
     */
    private TodoItem item;

    private SharedPreferences prefs;

    @Override
    public TodoItem readItem() {
        Log.i(logger, "readItem()...");

        loadPreferences();
        if (this.item == null) {
            this.item = new TodoItem(prefs.getLong(PREF_ITEM_ID, -1),
					prefs.getString(PREF_ITEM_NAME, ""),
					prefs.getString(PREF_ITEM_DESC, ""),
					prefs.getBoolean(String.valueOf(PREF_ITEM_FINISHED), false),
                    prefs.getBoolean(String.valueOf(PREF_ITEM_FAVORITE), false),
                    prefs.getLong(String.valueOf(PREF_ITEM_DUEDATE), 0));

            Log.i(logger, "readItem(): " + this.item);
        }

        return this.item;
    }

    @Override
    public void writeItem() {
        Log.i(logger, "writeItem(): " + this.item);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREF_ITEM_ID, this.item.getId());
        editor.putString(PREF_ITEM_NAME, this.item.getName());
        editor.putString(PREF_ITEM_DESC, this.item.getDescription());
        editor.putBoolean(String.valueOf(PREF_ITEM_FINISHED), this.item.isFinished());
        editor.putBoolean(String.valueOf(PREF_ITEM_FAVORITE), this.item.isFavorite());
        editor.putLong(String.valueOf(PREF_ITEM_DUEDATE), this.item.getDueDate());

        editor.commit();
    }

    @Override
    public boolean hasItem() {
        Log.i(logger, "hasItem()...");

        loadPreferences();
        return prefs.contains(PREF_ITEM_ID);
    }

    @Override
    public void createItem() {
        Log.i(logger, "createItem()...");

        this.item = new TodoItem(-1, "", "", false, false, 0L);
    }

    /**
     * access the preferences
     */
    private void loadPreferences() {
        if (this.prefs == null) {
            this.prefs = getActivity().getSharedPreferences(PREFERENCES_ID, Activity.MODE_PRIVATE);
            Log.i(logger, "loadPreferences(): " + this.prefs.getAll());
        }
    }

    @Override
    public void deleteItem() {
        Log.i(logger, "deleteItem()...");
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_ITEM_ID);
        editor.remove(PREF_ITEM_NAME);
        editor.remove(PREF_ITEM_DESC);
        editor.remove(String.valueOf(PREF_ITEM_FAVORITE));
        editor.remove(String.valueOf(PREF_ITEM_FINISHED));
        editor.remove(String.valueOf(PREF_ITEM_DUEDATE));

        editor.commit();
    }

}
