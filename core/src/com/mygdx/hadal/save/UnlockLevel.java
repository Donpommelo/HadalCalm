package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public enum UnlockLevel {

	HUB("Maps/test_map.tmx"),
	TUTORIAL_1("Maps/tutorial.tmx"),
	ARENA_1("Maps/arena_1.tmx"),
	ARENA_2("Maps/arena_2.tmx"),
	LEVEL_1("Maps/map_2.tmx"),
	LEVEL_2("Maps/expanse.tmx"),
	NASU("Maps/nasu.tmx"),
	
	;
	private String map;
	private Loadout loadout;
	private InfoLevel info;
	
	UnlockLevel(String map, Loadout loadout) {
		this(map);
		this.loadout = loadout;
	}
	
	UnlockLevel(String map) {
		this.map = map;
	}
	
	public static Array<UnlockLevel> getUnlocks(boolean unlock, UnlockTag... tags) {
		Array<UnlockLevel> items = new Array<UnlockLevel>();
		
		for (UnlockLevel u : UnlockLevel.values()) {
			
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getTags().size(); j++) {
					if (tags[i].equals(u.getTags().get(j))) {
						get = true;
					}
				}
			}
			
			if (unlock && !u.isUnlocked()) {
				get = false;
			}
			
			if (get) {
				items.add(u);
			}
		}
		
		return items;
	}
		
	public InfoLevel getInfo() {
		return info;
	}

	public void setInfo(InfoLevel info) {
		this.info = info;
	}

	public ArrayList<UnlockTag> getTags() {
		return info.getTags();
	}
	
	public String getMap() {
		return map;
	}
	
	public Loadout getLoadout() {
		return loadout;
	}

	public boolean isUnlocked() {
		return info.isUnlocked();
	}
	
	public String getName() {
		return info.getName();
	}
	
	public String getDescr() {
		return info.getDescription();
	}
	
	public void setUnlocked(boolean unlock) {
		info.setUnlocked(unlock);
	}

}
