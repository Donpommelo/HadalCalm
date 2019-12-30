package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.mygdx.hadal.save.UnlockManager.UnlockTag;

/**
 * An InfoItem represents the information for a single unlockable item to be loaded/saved to json
 * @author Zachary Tu
 *
 */
public class InfoItem {

	private String name;
	private String description;
	private String descriptionLong;

	private ArrayList<UnlockTag> tags;
	
	public InfoItem() {}

	public String getName() { return name; }

	public String getDescription() { return description; }
	
	public String getDescriptionLong() { return descriptionLong; }
	
	public ArrayList<UnlockTag> getTags() { return tags; }

	public void setName(String name) { this.name = name; }

	public void setDescription(String description) { this.description = description; }
	
	public void setDescriptionLong(String descriptionLong) { this.descriptionLong = descriptionLong; }

	public void setTags(ArrayList<UnlockTag> tags) { this.tags = tags; }
}
