package com.mygdx.hadal.equip;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.managers.AssetList;

public class Loadout {

	private final static int numSlots = 3;
	
	private final static int numArtifacts = 2;
	
	public UnlockEquip[] multitools;
	
	public Artifact[] artifacts;
	
	public String playerSprite;
	
	public Loadout() {
		multitools = new UnlockEquip[numSlots];
		multitools[0] = UnlockEquip.SPEARGUN;
		multitools[1] = UnlockEquip.SCRAPRIPPER;
		multitools[2] = UnlockEquip.MELON;
		
		artifacts = new Artifact[numArtifacts];
		
		playerSprite = AssetList.PLAYER_MOREAU_ATL.toString();
	}
	
	public Loadout(UnlockEquip... tools) {
		multitools = new UnlockEquip[numSlots];
		multitools[0] = UnlockEquip.NOTHING;
		multitools[1] = UnlockEquip.NOTHING;
		multitools[2] = UnlockEquip.NOTHING;
		
		for (int i = 0; i < numSlots; i++) {
			if (tools.length > i) {
				multitools[i] = tools[i];
			}
		}
		artifacts = new Artifact[numArtifacts];
		
		playerSprite = AssetList.PLAYER_MOREAU_ATL.toString();

	}
	
	public static int getNumSlots() {
		return numSlots;
	}
	
	public static int getNumArtifacts() {
		return numArtifacts;
	}
}
