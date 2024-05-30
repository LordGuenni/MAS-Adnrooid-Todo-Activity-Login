package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.properties;

import android.app.Activity;
import android.util.Log;
import de.thb.fbi.msr.maus.einkaufsliste.DataAccessApplication;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.preferences.PreferencesDataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

import java.io.IOException;
import java.util.Properties;

/**
 * Using a properties file for reading/writing a single item. Apart from this,
 * functionality is rather close to the one of {@link PreferencesDataItemAccessor}
 *
 * @author Joern Kreutel
 */
public class PropertiesDataItemAccessor extends AbstractActivityDataAccessor implements DataItemAccessor {

    private static final String PROP_ITEM_FINISHED = "dataItemFinished";
    private static final String PROP_ITEM_FAVORITE = "dataItemFavorite";
    private static final String PROP_ITEM_DUEDATE = "dataItemDueDate";
    /**
     * the logger
     */
    protected static final String logger = PropertiesDataItemAccessor.class.getName();

    /**
     * the filename
     */
    private static final String PROPERTIES_FILE = "dataitem.txt";

    /**
     * the item id property
     */
    private static final String PROP_ITEM_ID = "dataItemId";

    /**
     * the item name property
     */
    private static final String PROP_ITEM_NAME = "dataItemName";

    /**
     * the item description property
     */
    private static final String PROP_ITEM_DESC = "dataItemDescription";

    /**
     * the item type property
     */
    private static final String PROP_ITEM_TYPE = "dataItemType";

    /**
     * the properties
     */
    private Properties props;

    /**
     * the item
     */
    private TodoItem item;

    @Override
    public TodoItem readItem() {
        Log.i(logger, "readItem()...");

        loadProperties();
        if (this.item == null) {
            this.item = new TodoItem(
					Long.parseLong(props.getProperty(PROP_ITEM_ID, "-1")),
                    props.getProperty(PROP_ITEM_NAME, ""),
					props.getProperty(PROP_ITEM_DESC, ""),
					false, false, 0);

            Log.i(logger, "readItem(): " + this.item);
        }

        return this.item;
    }

    @Override
    public void writeItem() {
        Log.i(logger, "writeItem(): " + this.item);

        props.setProperty(PROP_ITEM_ID, String.valueOf(this.item.getId()));
        props.setProperty(PROP_ITEM_NAME, this.item.getName());
        props.setProperty(PROP_ITEM_DESC, this.item.getDescription());
        props.setProperty(PROP_ITEM_FINISHED, String.valueOf(this.item.isFinished()));
        props.setProperty(PROP_ITEM_FAVORITE, String.valueOf(this.item.isFavorite()));
        props.setProperty(PROP_ITEM_DUEDATE, String.valueOf(this.item.getDueDate()));


        writeProperties();
    }

    private void writeProperties() {
        try {
            props.store(getActivity().openFileOutput(PROPERTIES_FILE, Activity.MODE_APPEND), "");
            Log.i(logger, "writeProperties(): properties have been written: " + props);

        } catch (IOException e) {
            String err = "got exception trying to write properties: " + e;
            Log.e(logger, err, e);
            ((DataAccessApplication) getActivity().getApplication()).reportError(getActivity(), err);
        }
    }

    @Override
    public boolean hasItem() {
        Log.i(logger, "hasItem()...");

        loadProperties();
        return !"".equals(props.getProperty(PROP_ITEM_ID, ""));
    }

    @Override
    public void createItem() {
        Log.i(logger, "createItem()...");

        this.item = new TodoItem(-1,"", "", false,false,0L);
    }

    @Override
    public void deleteItem() {
        Log.i(logger, "deleteItem()...");
        this.props.setProperty(PROP_ITEM_ID, "");
        this.props.setProperty(PROP_ITEM_TYPE, "");
        this.props.setProperty(PROP_ITEM_NAME, "");
        this.props.setProperty(PROP_ITEM_DESC, "");

        writeProperties();
    }

    private void loadProperties() {
        this.props = new Properties();
        try {
            props.load(getActivity().openFileInput(PROPERTIES_FILE));
            Log.i(logger, "loadProperties(): properties have been loaded: "
                    + props);
        } catch (Exception e) {
            Log.w(logger, "got exception trying to read properties: " + e
                    + ". Maybe they do not exist so far. Try to create...", e);
            writeProperties();
        }
    }

}
