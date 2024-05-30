package de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.thb.fbi.msr.maus.einkaufsliste.model.api.TodoItem;

import java.util.List;

@Dao //Data Access Object
interface RoomDataItemDAO {

    @Insert
    void insert(TodoItem aTodoItem);

    @Query("SELECT * FROM TodoItem")
    List<TodoItem> getAllDataItems();

    @Query("DELETE FROM TodoItem")
    void deleteAllDataItems();

    @Update
    void update(TodoItem item);
}
