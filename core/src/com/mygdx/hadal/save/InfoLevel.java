package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public class InfoLevel {

	private String name;
	private String description;
	private String descriptionLong;

	private boolean unlocked;
	private ArrayList<UnlockTag> tags;
	
	public InfoLevel() {
	
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDescriptionLong() {
		return descriptionLong;
	}
	
	public ArrayList<UnlockTag> getTags() {
		return tags;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescriptionLong(String descriptionLong) {
		this.descriptionLong = descriptionLong;
	}
	
	public void setTags(ArrayList<UnlockTag> tags) {
		this.tags = tags;
	}
	
}
