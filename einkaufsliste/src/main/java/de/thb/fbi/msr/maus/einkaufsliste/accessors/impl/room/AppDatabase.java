package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

@Database(entities = {TodoItem.class}, version=1)
@TypeConverters(Converters.class)
abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase sInstance;

    public abstract RoomDataItemDAO dataItemDao();

    public static AppDatabase getInstance(final Context aContext) {
        if ( sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(aContext.getApplicationContext(), AppDatabase.class, "dataitem-database").build();
                }
            }
        }
        return sInstance;
    }
}
