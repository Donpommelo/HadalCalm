package com.mygdx.hadal.equip;

import com.mygdx.hadal.save.*;
import com.mygdx.hadal.server.AlignmentFilter;

import java.util.Arrays;

/**
 * A Loadout represents a player's tools, artifact, active item and character skin.
 * @author Jucker Jarpo
 */
public class Loadout {

	public static final int BASE_WEAPON_SLOTS = 3;
	public static final int MAX_WEAPON_SLOTS = 4;
	public static final int MAX_ARTIFACT_SLOTS = 12;
	public static final int MAX_COSMETIC_SLOTS = 12;

	public UnlockEquip[] multitools;
	public UnlockArtifact[] artifacts;
	public UnlockCosmetic[] cosmetics;
	public UnlockActives activeItem;
	public UnlockCharacter character;
	public AlignmentFilter team;
	
	public Loadout() {}
	
	/**
	 * This method loads a loadout from an input save record.
	 * Usually used when creating a brand new player.
	 */
	public Loadout(SavedLoadout loadout) {
		multitools = new UnlockEquip[MAX_WEAPON_SLOTS];
		artifacts = new UnlockArtifact[MAX_ARTIFACT_SLOTS];
		cosmetics = new UnlockCosmetic[MAX_COSMETIC_SLOTS];
		Arrays.fill(multitools, UnlockEquip.NOTHING);
		Arrays.fill(artifacts, UnlockArtifact.NOTHING);
		Arrays.fill(cosmetics, UnlockCosmetic.NOTHING_HAT1);

		for (int i = 0; i < MAX_WEAPON_SLOTS; i++) {
			if (loadout.getEquip().length > i) {
				multitools[i] = UnlockEquip.getByName(loadout.getEquip()[i]);
			}
		}
		
		for (int i = 0; i < MAX_ARTIFACT_SLOTS; i++) {
			if (loadout.getArtifact().length > i) {
				artifacts[i] = UnlockArtifact.getByName(loadout.getArtifact()[i]);
			}
		}

		for (int i = 0; i < MAX_COSMETIC_SLOTS; i++) {
			if (loadout.getCosmetic().length > i) {
				cosmetics[i] = UnlockCosmetic.getByName(loadout.getCosmetic()[i]);
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
		multitools = new UnlockEquip[MAX_WEAPON_SLOTS];
		artifacts = new UnlockArtifact[MAX_ARTIFACT_SLOTS];
		cosmetics = new UnlockCosmetic[MAX_COSMETIC_SLOTS];
		Arrays.fill(multitools, UnlockEquip.NOTHING);
		Arrays.fill(artifacts, UnlockArtifact.NOTHING);
		Arrays.fill(cosmetics, UnlockCosmetic.NOTHING_HAT1);
		System.arraycopy(old.multitools, 0, multitools, 0, MAX_WEAPON_SLOTS);
		System.arraycopy(old.artifacts, 0, artifacts, 0, MAX_ARTIFACT_SLOTS);
		System.arraycopy(old.cosmetics, 0, cosmetics, 0, MAX_COSMETIC_SLOTS);

		activeItem = old.activeItem;
		character = old.character;
		team = old.team;
	}
}
