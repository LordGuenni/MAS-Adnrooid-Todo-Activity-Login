package de.thb.fbi.msr.maus.einkaufsliste.model.api;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import de.thb.fbi.msr.maus.einkaufsliste.accessors.impl.room.Converters;

import java.io.Serializable;


/**
 * A single data item
 * 
 * @author Joern Kreutel
 * @author Martin Schafföner
 *
 */
@Entity
@TypeConverters(Converters.class)
public class TodoItem implements Serializable {

	/**
	 * some static id assignment
	 */
	private static int ID = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7481912314472891511L;

	/**
	 * the fields
	 */
	@PrimaryKey(autoGenerate = true)
	private long id;
	private String name;
	private String description;
	private boolean isFinished;



	private boolean isFavorite;
	private long dueDate;

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Ignore

	
	public TodoItem() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public TodoItem(String name, String description, boolean isFinished, boolean isFavorite, long dueDate) {
		this.name = name;
		this.description = description;
		this.isFinished = isFinished;
		this.isFavorite = isFavorite;
		this.dueDate = dueDate;
	}

	@Ignore
	public TodoItem(long id, String name, String description, boolean isFinished, boolean isFavorite, long dueDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.isFinished = isFinished;
		this.isFavorite = isFavorite;
		this.dueDate = dueDate;
	}

	/**
	 * TODO implement
	 * @return
	 */


	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean finished) {
		isFinished = finished;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	public void updateFrom(TodoItem item) {
		this.setName(item.getName());
		this.setDescription(item.getDescription());

	}
	
	public String toString() {
		return "{TodoItem " + this.getId() + " " + this.getName() + "}";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}
}
