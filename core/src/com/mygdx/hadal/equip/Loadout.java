package com.mygdx.hadal.equip;

import java.util.ArrayList;

import com.mygdx.hadal.save.Record;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;

public class Loadout {

	private final static int numSlots = 3;
	
	public UnlockEquip[] multitools;
	
	public UnlockArtifact startifact;
	public ArrayList<UnlockArtifact> artifacts;
	
	public UnlockActives activeItem;
	
	public UnlockCharacter character;
	
	public Loadout() {}
		
	public Loadout(Record record) {
		multitools = new UnlockEquip[numSlots];
		multitools[0] = UnlockEquip.NOTHING;
		multitools[1] = UnlockEquip.NOTHING;
		multitools[2] = UnlockEquip.NOTHING;
		
		artifacts = new ArrayList<UnlockArtifact>();
		
		for (int i = 0; i < numSlots; i++) {
			if (record.getEquips().length > i) {
				multitools[i] = UnlockEquip.valueOf(record.getEquips()[i]);
			}
		}
		
		startifact = UnlockArtifact.valueOf(record.getArtifact());
		activeItem = UnlockActives.valueOf(record.getActive());
		character = UnlockCharacter.valueOf(record.getCharacter());
	}
	
	public Loadout(Loadout old) {
		multitools = new UnlockEquip[numSlots];
		multitools[0] = old.multitools[0];
		multitools[1] = old.multitools[1];
		multitools[2] = old.multitools[2];
		
		artifacts = new ArrayList<UnlockArtifact>();

		for (UnlockArtifact artifact: old.artifacts) {
			artifacts.add(artifact);
		}
		
		startifact = old.startifact;
		activeItem = old.activeItem;
		character = old.character;
	}
	
	public static int getNumSlots() {
		return numSlots;
	}
}
