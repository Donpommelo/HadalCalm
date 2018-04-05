package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public class InfoEquip {

	private String name;
	private String description;
	private int cost;
	private boolean unlocked;
	private ArrayList<UnlockTag> tags;
	
	public InfoEquip() {
	
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getCost() {
		return cost;
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

	public void setCost(int cost) {
		this.cost = cost;
	}

	public void setTags(ArrayList<UnlockTag> tags) {
		this.tags = tags;
	}
	
}
