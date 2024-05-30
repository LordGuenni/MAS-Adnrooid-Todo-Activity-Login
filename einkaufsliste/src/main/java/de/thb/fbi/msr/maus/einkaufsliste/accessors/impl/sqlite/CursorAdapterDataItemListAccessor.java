package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.sqlite;

import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.AsyncTaskLoader;
import android.content.Context;
import androidx.loader.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import de.thb.fbi.msr.maus.einkaufsliste.R;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.shared.AbstractActivityDataAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.spi.DataItemListAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

/**
 * uses a CursorAdapter for providing access to the list of TodoItem
 * 
 * @author Joern Kreutel
 * @author Martin Schafföner
 *
 */
public class CursorAdapterDataItemListAccessor extends
        AbstractActivityDataAccessor implements DataItemListAccessor {

	protected static final String logger = CursorAdapterDataItemListAccessor.class
			.getName();
	
	// should usually reside in the activity#
	// we have it here for abstraction reasons, since the activity should be able to swap different IDataItemListAccessor
	// implementations
	private static final int LOADER_ID = 1;

	private SimpleCursorAdapter adapter;

	private LoaderCallbacks<Cursor> mCallbacks;

	private SQLiteDBHelper mHelper;

	// code adapted from Bill Phillips and Brian Hardy: "Android Programming - The Big Nerd Ranch Guide"
	private static abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor>
	{
		private Cursor mCursor;

		// template method pattern
		protected abstract Cursor loadCursor();

		public SQLiteCursorLoader(Context aContext) {
			super(aContext);
		}

		@Override
		public Cursor loadInBackground() {
			final Cursor cursor = loadCursor();
			if (cursor != null)
			{
				cursor.getCount();
			}
			return cursor;
		}

		@Override
		public void onCanceled(Cursor aCursor) {
			if (aCursor != null && !aCursor.isClosed())
			{
				aCursor.close();
			}
		}

		@Override
		public void deliverResult(Cursor aData) {
			Cursor oldCursor = mCursor;
			mCursor = aData;
			if (isStarted())
			{
				super.deliverResult(aData);
			}
			
			if (oldCursor != null && oldCursor != aData && !oldCursor.isClosed())
			{
				oldCursor.close();
			}
		}

		@Override
		protected void onReset() {
			super.onReset();
			
			onStopLoading();
		}

		@Override
		protected void onStartLoading() {
			if (mCursor != null)
			{
				deliverResult(mCursor);
			}
			if (takeContentChanged() || mCursor == null)
			{
				forceLoad();
			}
		}

		@Override
		protected void onStopLoading() {	
			cancelLoad();
			if (mCursor != null && !mCursor.isClosed())
			{
				mCursor.close();
			}
		}
	}
	
	private static class MyCursorLoader extends SQLiteCursorLoader
	{

		private SQLiteDBHelper mCursorFactory;

		public MyCursorLoader(Context aContext, SQLiteDBHelper aCursorFactory) {
			super(aContext);
			mCursorFactory = aCursorFactory;
		}

		@Override
		protected Cursor loadCursor() {
			return mCursorFactory.getCursor();
		}
		
	}
	private class MyLoaderCallbacks implements LoaderCallbacks<Cursor>
	{

		@Override
		public Loader<Cursor> onCreateLoader(int aId, Bundle aArgs) {
			return new MyCursorLoader(getActivity(), mHelper);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> aLoader, Cursor aCursor) {
			if(aLoader.getId() == LOADER_ID)
			{
				adapter.swapCursor(aCursor);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> aArg0) {
			adapter.swapCursor(null);
		}
		
	}
	
	@Override
	public ListAdapter getAdapter() {

		mHelper = new SQLiteDBHelper(getActivity());
		mHelper.prepareSQLiteDatabase();
		
		LoaderManager loaderManager = getActivity().getSupportLoaderManager();
		mCallbacks = new MyLoaderCallbacks();
		loaderManager.initLoader(LOADER_ID, null, mCallbacks);

		/*
		 * create a cursor adapter that maps the "name" column in the db to the
		 * itemName element in the view
		 * 
		 * (i.e. using this adapter there is no need to create TodoItem objects
		 * for all items that are contained in the db)
		 */
		this.adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_in_listview,
				null,
				new String[] { SQLiteDBHelper.COL_NAME, SQLiteDBHelper.COL_TYPE },
				new int[] { R.id.itemName, R.id.itemType }, 0);

		return this.adapter;
	}

	/**
	 * manipulate the db. In all cases, the cursor needs to be requeried in
	 * order for the view to be updated
	 */

	@Override
	public void addItem(TodoItem item) {
		mHelper.addItemToDb(item);
		getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, mCallbacks);
	}

	@Override
	public void updateItem(TodoItem item) {
		mHelper.updateItemInDb(item);
		getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, mCallbacks);
	}

	@Override
	public void deleteItem(TodoItem item) {
		mHelper.removeItemFromDb(item);
		getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, mCallbacks);
	}

	@Override
	public TodoItem getSelectedItem(int itemPosition, long itemId) {
		return mHelper.createItemFromCursor((Cursor) this.adapter.getItem(itemPosition));
	}

	@Override
	public void close() {
		mHelper.close();
	}

}
