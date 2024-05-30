package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.thb.fbi.msr.maus.einkaufsliste.R;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

public class DataItemListViews {
	public static  ArrayAdapter<TodoItem> createDataItemArrayAdapter(final Activity aContext, final List<TodoItem> aItems) {
		return new ArrayAdapter<TodoItem>(aContext, R.layout.item_in_listview, aItems) {

			@Override
			public View getView(int position, View listItemView, ViewGroup parent) {
				View layout = listItemView == null ? aContext.getLayoutInflater().inflate(
						R.layout.item_in_listview, parent, false) : listItemView;
				// getActivity().getLayoutInflater().inflate(
				// R.layout.item_in_listview, listView, false);
				final TextView itemView = layout.findViewById(R.id.itemName);
				itemView.setText(getItem(position).getName());

				
				final TextView typeView = layout.findViewById(R.id.itemType);
				typeView.setText(getItem(position).toString());
				typeView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View aClickedView) {
						Toast.makeText(getContext(), typeView.getText(), Toast.LENGTH_LONG).show();
						
					}
				});
				return layout;
			}

		};
	}

}
