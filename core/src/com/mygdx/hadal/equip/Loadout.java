package com.mygdx.hadal.equip;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;

public class Loadout {

	private final static int numSlots = 3;
	
	private final static int numArtifacts = 2;
	
	public UnlockEquip[] multitools;
	
	public UnlockArtifact[] artifacts;
	
	public UnlockCharacter playerSprite;
	
	public Loadout() {
		multitools = new UnlockEquip[numSlots];
		multitools[0] = UnlockEquip.SPEARGUN;
		multitools[1] = UnlockEquip.SCRAPRIPPER;
		multitools[2] = UnlockEquip.MELON;
		
		artifacts = new UnlockArtifact[numArtifacts];
		
		playerSprite = UnlockCharacter.MOREAU;
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
		artifacts = new UnlockArtifact[numArtifacts];
		
		playerSprite = UnlockCharacter.MOREAU;

	}
	
	public static int getNumSlots() {
		return numSlots;
	}
	
	public static int getNumArtifacts() {
		return numArtifacts;
	}
}
