package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public class InfoCharacter {

	private String name, description, bio;
	private int cost;
	private boolean unlocked;
	private ArrayList<UnlockTag> tags;
	
	public InfoCharacter() {
	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}

	public ArrayList<UnlockTag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<UnlockTag> tags) {
		this.tags = tags;
	}
}
