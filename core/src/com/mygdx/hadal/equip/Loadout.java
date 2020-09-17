package com.mygdx.hadal.equip;

import java.util.Arrays;

import com.mygdx.hadal.save.SavedLoadout;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;

/**
 * A Loadout represent's a player's tools, artifact, active item and character skin.
 * @author Zachary Tu
 */
public class Loadout {

	public final static int baseWeaponSlots = 3;
	public final static int maxWeaponSlots = 4;
	
	public final static int maxArtifactSlots = 12;
	
	public UnlockEquip[] multitools;
	public UnlockArtifact[] artifacts;
	public UnlockActives activeItem;
	public UnlockCharacter character;
	
	public Loadout() {}
	
	/**
	 * This method loads a loadout from an input save record.
	 * Usually used when creating a brand new player.
	 */
	public Loadout(SavedLoadout loadout) {
		multitools = new UnlockEquip[maxWeaponSlots];
		artifacts = new UnlockArtifact[maxArtifactSlots];
		Arrays.fill(multitools, UnlockEquip.NOTHING);
		Arrays.fill(artifacts, UnlockArtifact.NOTHING);
		
		for (int i = 0; i < maxWeaponSlots; i++) {
			if (loadout.getEquips().length > i) {
				multitools[i] = UnlockEquip.valueOf(loadout.getEquips()[i]);
			}
		}
		
		for (int i = 0; i < maxArtifactSlots; i++) {
			if (loadout.getArtifacts().length > i) {
				artifacts[i] = UnlockArtifact.valueOf(loadout.getArtifacts()[i]);
			}
		}
		
		activeItem = UnlockActives.valueOf(loadout.getActive());
		character = UnlockCharacter.valueOf(loadout.getCharacter());
	}
	
	/**
	 * This generates a new loadout from a prexisting one.
	 */
	public Loadout(Loadout old) {
		multitools = new UnlockEquip[maxWeaponSlots];
		artifacts = new UnlockArtifact[maxArtifactSlots];
		Arrays.fill(multitools, UnlockEquip.NOTHING);
		Arrays.fill(artifacts, UnlockArtifact.NOTHING);
		
		for (int i = 0; i < maxWeaponSlots; i++) {
			multitools[i] = old.multitools[i];
		}
		
		for (int i = 0; i < maxArtifactSlots; i++) {
			artifacts[i] = old.artifacts[i];
		}

		activeItem = old.activeItem;
		character = old.character;
	}
}
