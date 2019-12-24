package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public class InfoItem {

	private String name;
	private String description;
	private String descriptionLong;

	private int cost;
	private boolean unlocked;
	private ArrayList<UnlockTag> tags;
	
	public InfoItem() {}

	public String getName() { return name; }

	public String getDescription() { return description; }
	
	public String getDescriptionLong() { return descriptionLong; }
	
	public int getCost() { return cost; }
	
	public ArrayList<UnlockTag> getTags() { return tags; }

	public boolean isUnlocked() { return unlocked; }

	public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

	public void setName(String name) { this.name = name; }

	public void setDescription(String description) { this.description = description; }
	
	public void setDescriptionLong(String descriptionLong) { this.descriptionLong = descriptionLong; }

	public void setCost(int cost) { this.cost = cost; }

	public void setTags(ArrayList<UnlockTag> tags) { this.tags = tags; }
}
