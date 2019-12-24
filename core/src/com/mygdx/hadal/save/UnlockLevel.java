package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public enum UnlockLevel {

	HUB("Maps/SS_Tunicate.tmx"),
	HUB_MULTI("Maps/SS_Tunicate_Multi.tmx"),
	TUTORIAL_1("Maps/tutorial1.tmx", UnlockArtifact.NOTHING, UnlockActives.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING),
	ARENA_1("Maps/arena_1.tmx"),
	ARENA_2("Maps/arena_2.tmx"),
	LEVEL_1("Maps/map_2.tmx"),
	LEVEL_2("Maps/expanse.tmx"),
	LEVEL_3("Maps/aye_carambas.tmx"),
	NASU("Maps/nasu.tmx"),
	DONT_FALL("Maps/DontFall.tmx"),
	DM_GULLY("Maps/dm_gully.tmx", null, null, UnlockEquip.SPEARGUN, UnlockEquip.SCRAPRIPPER, UnlockEquip.NOTHING),
	BOSS1("Maps/Boss1.tmx"),
	
	;
	private String map;
	private InfoLevel info;
	
	private UnlockEquip[] multitools;
	private UnlockArtifact startifact;
	private UnlockActives activeItem;
	
	UnlockLevel(String map, UnlockArtifact startifact, UnlockActives activeItem, UnlockEquip... multitools) {
		this(map);
		this.startifact = startifact;
		this.activeItem = activeItem;
		this.multitools = multitools;
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
		
	public InfoLevel getInfo() { return info; }

	public void setInfo(InfoLevel info) { this.info = info; }

	public ArrayList<UnlockTag> getTags() {	return info.getTags(); }
	
	public String getMap() { return map; }

	public boolean isUnlocked() { return info.isUnlocked(); }
	
	public String getName() { return info.getName(); }
	
	public String getDescr() { return info.getDescription(); }
	
	public String getDescrLong() { return info.getDescriptionLong(); }
	
	public void setUnlocked(boolean unlock) { info.setUnlocked(unlock); }

	public UnlockEquip[] getMultitools() { return multitools; }

	public void setMultitools(UnlockEquip[] multitools) { this.multitools = multitools; }

	public UnlockArtifact getStartifact() {	return startifact; }

	public void setStartifact(UnlockArtifact startifact) { this.startifact = startifact; }

	public UnlockActives getActiveItem() { return activeItem; }

	public void setActiveItem(UnlockActives activeItem) { this.activeItem = activeItem; }	
}
