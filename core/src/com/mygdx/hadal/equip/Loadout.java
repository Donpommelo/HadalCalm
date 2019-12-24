package com.mygdx.hadal.equip;

import java.util.ArrayList;

import com.mygdx.hadal.save.Record;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;

/**
 * A Loadout represent's a player's tools, artifact, active item and character skin.
 * @author Zachary Tu
 *
 */
public class Loadout {

	public final static int baseSlots = 3;
	public final static int maxSlots = 4;
	
	public UnlockEquip[] multitools;
	public UnlockArtifact startifact;
	public ArrayList<UnlockArtifact> artifacts;
	public UnlockActives activeItem;
	public UnlockCharacter character;
	
	public Loadout() {}
	
	/**
	 * This method loads a loadout from an input save record.
	 * Usually used when creating a brand new player.
	 */
	public Loadout(Record record) {
		multitools = new UnlockEquip[maxSlots];
		
		for (int i = 0; i < maxSlots; i++) {
			multitools[i] = UnlockEquip.NOTHING;
		}
		
		artifacts = new ArrayList<UnlockArtifact>();
		
		for (int i = 0; i < maxSlots; i++) {
			if (record.getEquips().length > i) {
				multitools[i] = UnlockEquip.valueOf(record.getEquips()[i]);
			}
		}
		
		startifact = UnlockArtifact.valueOf(record.getArtifact());
		activeItem = UnlockActives.valueOf(record.getActive());
		character = UnlockCharacter.valueOf(record.getCharacter());
	}
	
	/**
	 * This generates a new loadout from a prexisting one.
	 */
	public Loadout(Loadout old) {
		multitools = new UnlockEquip[maxSlots];
		for (int i = 0; i < maxSlots; i++) {
			multitools[i] = old.multitools[i];
		}

		artifacts = new ArrayList<UnlockArtifact>();

		for (UnlockArtifact artifact: old.artifacts) {
			artifacts.add(artifact);
		}
		
		startifact = old.startifact;
		activeItem = old.activeItem;
		character = old.character;
	}
}
