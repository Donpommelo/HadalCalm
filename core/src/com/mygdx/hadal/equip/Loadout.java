package com.mygdx.hadal.equip;

import com.mygdx.hadal.save.*;
import com.mygdx.hadal.server.AlignmentFilter;

import java.util.Arrays;

/**
 * A Loadout represents a player's tools, artifact, active item and character skin.
 * @author Jucker Jarpo
 */
public class Loadout {

	public static final int baseWeaponSlots = 3;
	public static final int maxWeaponSlots = 4;
	
	public static final int maxArtifactSlots = 12;
	
	public UnlockEquip[] multitools;
	public UnlockArtifact[] artifacts;
	public UnlockActives activeItem;
	public UnlockCharacter character;
	public AlignmentFilter team;
	
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
				multitools[i] = UnlockEquip.getByName(loadout.getEquips()[i]);
			}
		}
		
		for (int i = 0; i < maxArtifactSlots; i++) {
			if (loadout.getArtifacts().length > i) {
				artifacts[i] = UnlockArtifact.getByName(loadout.getArtifacts()[i]);
			}
		}
		
		activeItem = UnlockActives.getByName(loadout.getActive());
		character = UnlockCharacter.getByName(loadout.getCharacter());
		team = AlignmentFilter.getByName(loadout.getTeam());
	}
	
	/**
	 * This generates a new loadout from a preexisting one.
	 */
	public Loadout(Loadout old) {
		multitools = new UnlockEquip[maxWeaponSlots];
		artifacts = new UnlockArtifact[maxArtifactSlots];
		Arrays.fill(multitools, UnlockEquip.NOTHING);
		Arrays.fill(artifacts, UnlockArtifact.NOTHING);

		System.arraycopy(old.multitools, 0, multitools, 0, maxWeaponSlots);
		System.arraycopy(old.artifacts, 0, artifacts, 0, maxArtifactSlots);

		activeItem = old.activeItem;
		character = old.character;
		team = old.team;
	}
}
