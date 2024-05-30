package de.thb.fbi.msr.maus.einkaufsliste.accessors;

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.DataItemListViews;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;
import de.thb.fbi.msr.maus.einkaufsliste.model.spi.DataAccessResourceCRUDHandler;

import java.util.List;

/**
 * Using a remote backend for handling items (actually, the fact that it is a
 * *remote* backend is completely hidden by accessing the application object as
 * {@link DataAccessResourceCRUDHandler})
 *
 * @author Joern Kreutel
 */
public class RemoteBackendDataItemListAccessor extends AbstractActivityDataAccessor implements DataItemListAccessor {

    private List<TodoItem> items;

    private ArrayAdapter<TodoItem> adapter;

    @Override
    public void addItem(TodoItem item) {
        getRemoteBackend().createDataItem(item);
        this.items.add(item);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public ListAdapter getAdapter() {
        this.items = getRemoteBackend().readAllDataItem();
        // create the adapter from the list and override getView such that
        // the item names will be displayed
        adapter = DataItemListViews.createDataItemArrayAdapter(getActivity(), items);

        return adapter;
    }

    @Override
    public void updateItem(TodoItem item) {
        getRemoteBackend().updateDataItem(item);
        lookupItem(item).updateFrom(item);
        this.adapter.notifyDataSetChanged();
    }

    private DataAccessResourceCRUDHandler getRemoteBackend() {
        return (DataAccessResourceCRUDHandler) getActivity().getApplication();
    }

    @Override
    public void deleteItem(TodoItem item) {
        getRemoteBackend().deleteDataItem(item.getId());
        this.items.remove(lookupItem(item));
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public TodoItem getSelectedItem(int itemPosition, long itemId) {
        return adapter.getItem(itemPosition);
    }

    @Override
    public void close() {
        // nothing to do
    }

    /**
     * get the item from the list, checking identity of ids (as the argument
     * value may have been serialised/deserialised we cannot check for identity
     * of objects)
     */
    private TodoItem lookupItem(TodoItem item) {
        for (TodoItem current : this.items) {
            if (current.getId() == item.getId()) {
                return current;
            }
        }
        return null;
    }

}
