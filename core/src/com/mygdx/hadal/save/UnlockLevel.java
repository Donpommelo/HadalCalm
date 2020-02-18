package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

public enum UnlockLevel {

	HUB_MULTI("Maps/SS_Tunicate_Multi.tmx"),
	NASU("Maps/nasu.tmx"),
	BOSS1("Maps/Boss1.tmx"),
	BOSS2("Maps/Boss2.tmx"),
	
	SANDBOX("Maps/sandbox.tmx"),
	
	DM_CAROUSEL("Maps/dm_carousel.tmx"),
	DM_PINWHEEL("Maps/dm_pinwheel.tmx"),
	DM_GULLY("Maps/dm_gully.tmx"),
	DM_SAWMILL("Maps/dm_sawmill.tmx"),
	
	WRECK1("Maps/wreck1.tmx"),
	WRECK2("Maps/wreck2.tmx"),
	DERELICT1("Maps/derelict1.tmx"),
	WRECKUPPER1("Maps/wreckupper1.tmx"),
	WRECKUPPER2("Maps/wreckupper2.tmx"),
	WRECKUPPERSHAFT("Maps/wreckuppershaft.tmx"),
	WRECKEXPANSE1("Maps/wreckexpanse1.tmx"),
	WRECKEXPANSE2("Maps/wreckexpanse2.tmx"),
	WRECKEXPANSEBOSS("Maps/wreckexpanseboss.tmx"),
	SSTUNICATE1("Maps/sstunicate1.tmx"),
	PLENUMCHAMBER1("Maps/plenumchamber1.tmx"),
	NOISELESSSEA1("Maps/noiselesssea1.tmx"),

	
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
