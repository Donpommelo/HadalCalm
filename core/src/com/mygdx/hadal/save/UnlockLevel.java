package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Loadout;

public enum UnlockLevel {

	HUB("Maps/test_map.tmx", "Hub"),
	TUTORIAL_1("Maps/tutorial.tmx", "Tutorial 1", new Loadout(UnlockEquip.NOTHING), LevelTag.NAVIGATIONS, LevelTag.TRAINING),
	ARENA_1("Maps/arena_1.tmx", "Arena 1", LevelTag.NAVIGATIONS, LevelTag.ARENA),
	ARENA_2("Maps/arena_2.tmx", "Arena 2", LevelTag.NAVIGATIONS, LevelTag.ARENA),
	LEVEL_1("Maps/map_2.tmx", "Level 1", LevelTag.NAVIGATIONS, LevelTag.CAMPAIGN),
	LEVEL_2("Maps/expanse.tmx", "Level 2: Expanse", LevelTag.NAVIGATIONS, LevelTag.CAMPAIGN),
	NASU("Maps/nasu.tmx", "nasu", LevelTag.NAVIGATIONS, LevelTag.NASU),
	
	;
	private String map, name;
	private Loadout loadout;
	private boolean unlocked;
	private LevelTag[] tags;
	
	UnlockLevel(String map, String name, Loadout loadout, LevelTag... tags) {
		this(map, name, tags);
		this.loadout = loadout;
	}
	
	UnlockLevel(String map, String name, LevelTag... tags) {
		this.map = map;
		this.name= name;
		this.tags = tags;
		this.unlocked = true;
	}

	public static Array<UnlockLevel> getUnlocks(boolean unlock, LevelTag... tags) {
		Array<UnlockLevel> items = new Array<UnlockLevel>();
		
		for (UnlockLevel u : UnlockLevel.values()) {
			
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getTags().length; j++) {
					if (tags[i].equals(u.getTags()[j])) {
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
	
	public LevelTag[] getTags() {
		return tags;
	}
	
	public String getMap() {
		return map;
	}
	
	public Loadout getLoadout() {
		return loadout;
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
	
	public enum LevelTag {
		NAVIGATIONS,
		TRAINING,
		CAMPAIGN,
		ARENA,
		NASU,
		MISC
	}
}
