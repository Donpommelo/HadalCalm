package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

public enum UnlockLevel {

	HUB("Maps/SS_Tunicate.tmx"),
	HUB_MULTI("Maps/SS_Tunicate_Multi.tmx"),
	NASU("Maps/nasu.tmx"),
	DM_GULLY("Maps/dm_gully.tmx"),
	BOSS1("Maps/Boss1.tmx"),
	BOSS2("Maps/Boss2.tmx"),
	SANDBOX("Maps/sandbox.tmx"),
	SAWMILL("Maps/Sawmill.tmx"),
	
	;
	private String map;
	private InfoItem info;
	
	private UnlockEquip[] multitools;
	private UnlockArtifact[] artifacts;
	private UnlockActives activeItem;
	
	UnlockLevel(String map, UnlockArtifact[] artifacts, UnlockActives activeItem, UnlockEquip[] multitools) {
		this(map);
		this.artifacts = artifacts;
		this.activeItem = activeItem;
		this.multitools = multitools;
	}
	
	UnlockLevel(String map) {
		this.map = map;
	}
	
	public static Array<UnlockLevel> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockLevel> items = new Array<UnlockLevel>();
		
		for (UnlockLevel u : UnlockLevel.values()) {
			
			boolean get = UnlockManager.checkTags(u.getInfo(), tags);
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.LEVEL, u.toString())) {
				get = false;
			}
			
			if (get) {
				items.add(u);
			}
		}
		
		return items;
	}
		
	public InfoItem getInfo() { return info; }

	public void setInfo(InfoItem info) { this.info = info; }
	
	public String getMap() { return map; }
	
	public UnlockEquip[] getMultitools() { return multitools; }

	public void setMultitools(UnlockEquip[] multitools) { this.multitools = multitools; }

	public UnlockArtifact[] getArtifacts() { return artifacts; }

	public void setArtifacts(UnlockArtifact[] artifacts) { this.artifacts = artifacts; }

	public UnlockActives getActiveItem() { return activeItem; }

	public void setActiveItem(UnlockActives activeItem) { this.activeItem = activeItem; }	
}
