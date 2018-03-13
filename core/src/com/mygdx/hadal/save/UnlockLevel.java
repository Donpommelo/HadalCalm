package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;

public enum UnlockLevel {

	HUB("Maps/test_map.tmx", "Hub"),
	TUTORIAL_1("Maps/tutorial.tmx", "Tutorial 1"),
	ARENA_1("Maps/arena_1.tmx", "Arena 1"),
	ARENA_2("Maps/arena_2.tmx", "Arena 2"),
	LEVEL_1("Maps/map_2.tmx", "Level 1"),
	
	;
	private String map, name;
	private boolean unlocked;

	UnlockLevel(String map, String name) {
		this.map = map;
		this.name= name;
		this.unlocked = true;
	}

	public static Array<UnlockLevel> getUnlocks() {
		Array<UnlockLevel> items = new Array<UnlockLevel>();
		
		for (UnlockLevel u : UnlockLevel.values()) {
			if (u.isUnlocked()) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public String getMap() {
		return map;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
}
