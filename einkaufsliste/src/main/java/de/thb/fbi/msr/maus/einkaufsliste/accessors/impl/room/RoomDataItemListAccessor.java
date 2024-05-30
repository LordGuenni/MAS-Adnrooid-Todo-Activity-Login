package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.thb.fbi.msr.maus.einkaufsliste.ItemDetailsActivity;
import de.thb.fbi.msr.maus.einkaufsliste.R;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.intent.IntentDataItemAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;
import de.thb.fbi.msr.maus.einkaufsliste.DataAccessActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static de.thb.fbi.msr.maus.einkaufsliste.ItemListActivity.ARG_ITEM_OBJECT;
import static de.thb.fbi.msr.maus.einkaufsliste.ItemListActivity.REQUEST_ITEM_DETAILS;

public class RoomDataItemListAccessor extends AbstractActivityDataAccessor implements DataItemListAccessor {

    private RoomDataItemDAO mDAO;
    private ArrayAdapter<TodoItem> mAdapter;
    private AppDatabase mAppDB;

    @Override
    public void setActivity(AppCompatActivity activity) {
        super.setActivity(activity);
        init();
    }

    private void init() {
        mAppDB = AppDatabase.getInstance(getActivity());
        mDAO = mAppDB.dataItemDao();
    }

    @Override
    public void addItem(final TodoItem aItem) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                mDAO.insert(aItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.add(aItem);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public ListAdapter getAdapter() {
        final List<TodoItem> items = new ArrayList<>();

        mAdapter = new ArrayAdapter<TodoItem>(getActivity(), R.layout.item_in_listview, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_in_listview, parent, false);
                }

                TodoItem item = getItem(position);

                CheckBox itemStatus = convertView.findViewById(R.id.item_done);
                TextView itemName = convertView.findViewById(R.id.itemName);
                ImageView myImageView = convertView.findViewById(R.id.my_image_view);


                itemStatus.setChecked(item.isFinished());
                itemName.setText(item.getName());

                if (item.isFavorite()) {
                    myImageView.setImageResource(R.drawable.star_yellow);
                } else {
                    myImageView.setImageResource(R.drawable.star_grey);
                }

                itemStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the current status of the checkbox
                        boolean isChecked = ((CheckBox) v).isChecked();
                        // Set the done status of the TodoItem based on the checkbox status
                        item.setFinished(isChecked);
                        // Update the item in the database
                        updateItem(item);
                    }
                });

                itemName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Log.i(logger, "processItemSelection(): " + item);
                        // create an intent for opening the details view
                        Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
                        // pass the item to the intent
                        intent.putExtra(ARG_ITEM_OBJECT, item);
                        // also specify the accessor class
                        intent.putExtra(DataAccessActivity.ARG_ACCESSOR_CLASS, IntentDataItemAccessor.class.getName());
                        // start the details activity with the intent
                        ((AppCompatActivity) getContext()).startActivityForResult(intent, REQUEST_ITEM_DETAILS);
                    }
                });

                myImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setFavorite(!item.isFavorite());
                        updateItem(item);
                    }
                });

                return convertView;
            }
        };

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                mAdapter.setNotifyOnChange(false);
                List<TodoItem> items = mDAO.getAllDataItems();

                mAdapter.addAll(items);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
                reloadItems();
            }
        }.execute();
        return mAdapter;
    }

    @Override
    public void updateItem(TodoItem item) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                mDAO.update(item);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
                reloadItems();
            }

        }.execute();
    }

    @Override
    public void deleteItem(TodoItem item) {
        new AsyncTask<Void, Void, Void>()  {

            @Override
            protected Void doInBackground(Void... voids) {
                mDAO.deleteAllDataItems();
                mAdapter.clear();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
        mDAO.deleteAllDataItems();

    }

    public void reloadItems() {
        new AsyncTask<Void, Void, List<TodoItem>>() {
            @Override
            protected List<TodoItem> doInBackground(Void... voids) {
                List<TodoItem> items = mDAO.getAllDataItems();

                // Sort the items
                items.sort(new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem todo1, TodoItem todo2) {
                        // Unfinished todos before finished todos
                        if (todo1.isFinished() != todo2.isFinished()) {
                            return todo1.isFinished() ? 1 : -1;
                        }
                        // Within the same group, sort by due date
                        return Long.compare(todo1.getDueDate(), todo2.getDueDate());
                    }
                });

                return items;
            }

            @Override
            protected void onPostExecute(List<TodoItem> items) {
                mAdapter.clear();
                mAdapter.addAll(items);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public TodoItem getSelectedItem(int itemPosition, long itemId) {
        throw new UnsupportedOperationException("needs to be implemented!!!");
        //return null;
    }

    @Override
    public void close() {
        mAppDB.close();
    }
}


