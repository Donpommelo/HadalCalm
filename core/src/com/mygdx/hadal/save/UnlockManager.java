package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

import java.util.Objects;

/**
 * The UnlockManager manages the player's unlocked weapons, artifacts, etc
 * @author Proctavio Prolkner
 */
public class UnlockManager {
	
	/**
	 * This retrieves the player's unlocks from a file
	 */
	public static void retrieveItemInfo() {

		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Equips.json"))) {
			UnlockEquip.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Artifacts.json"))) {
			UnlockArtifact.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Actives.json"))) {
			UnlockActives.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Characters.json"))) {
			UnlockCharacter.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Cosmetics.json"))) {
			UnlockCosmetic.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Levels.json"))) {
			UnlockLevel.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Modes.json"))) {
			GameMode.getByName(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.json)));
		}
	}
	
	/**
	 * This acquires the information about an unlock based on its type and name
	 */
	public static InfoItem getInfo(UnlockType type, String name) {
		return switch (type) {
			case ACTIVE -> UnlockActives.getByName(name).getInfo();
			case ARTIFACT -> UnlockArtifact.getByName(name).getInfo();
			case CHARACTER -> UnlockCharacter.getByName(name).getInfo();
			case EQUIP -> UnlockEquip.getByName(name).getInfo();
			case COSMETIC -> UnlockCosmetic.getByName(name).getInfo();
			case LEVEL -> UnlockLevel.getByName(name).getInfo();
		};
	}
	
	/**
	 * This is used to determine which unlockitems will be available from a given hub event
	 * @param item: the item to check
	 * @param tags: a list of tags
	 * @return whether the item contains any of the tags
	 */
	public static boolean checkTags(InfoItem item, Array<UnlockTag> tags) {

		for (UnlockTag tag : tags) {
			boolean tagPresent = false;

			if (item == null) {
				return false;
			}
			if (tag.equals(UnlockTag.ALL)) {
				tagPresent = true;
			} else {
				for (int j = 0; j < item.getTags().size; j++) {
					if (tag.equals(item.getTags().get(j))) {
						tagPresent = true;
						break;
					}
				}
			}
			if (!tagPresent) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This returns if a certain unlock item is unlocked or not
	 */
	public static boolean checkUnlock(PlayState state, UnlockType type, String name) {
		return switch (type) {
			case ACTIVE -> state.getGsm().getRecord().getUnlockActive().get(name, false);
			case ARTIFACT -> state.getGsm().getRecord().getUnlockArtifact().get(name, false);
			case CHARACTER -> state.getGsm().getRecord().getUnlockCharacter().get(name, false);
			case EQUIP -> state.getGsm().getRecord().getUnlockEquip().get(name, false);
			case COSMETIC -> state.getGsm().getRecord().getUnlockCosmetic().get(name, false);
			case LEVEL -> state.getGsm().getRecord().getUnlockLevel().get(name, false);
		};
	}
	
	/**
	 * This sets a certain unlock item to be unlocked or locked. If unlocked, a notification appears
	 * After setting, the data is saved into the player's saves
	 */
	public static void setUnlock(PlayState state, UnlockType type, String name, boolean unlock) {
		switch(type) {
		case ACTIVE:
			state.getGsm().getRecord().getUnlockActive().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", HText.UNLOCK_ACTIVE.text(Objects.requireNonNull(getInfo(type, name)).getName()),
					"", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
			break;
		case ARTIFACT:
			state.getGsm().getRecord().getUnlockArtifact().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", HText.UNLOCK_ARTIFACT.text(Objects.requireNonNull(getInfo(type, name)).getName()),
					"", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
			break;
		case CHARACTER:
			state.getGsm().getRecord().getUnlockCharacter().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", HText.UNLOCK_CHARACTER.text(Objects.requireNonNull(getInfo(type, name)).getName()),
					"", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
			break;
		case EQUIP:
			state.getGsm().getRecord().getUnlockEquip().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", HText.UNLOCK_WEAPON.text(Objects.requireNonNull(getInfo(type, name)).getName()),
					"", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
			break;
		case LEVEL:
			state.getGsm().getRecord().getUnlockLevel().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", HText.UNLOCK_LEVEL.text(Objects.requireNonNull(getInfo(type, name)).getName()),
					"", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
			break;
		default:
		}
		state.getGsm().getRecord().saveRecord();
	}
	
	public enum UnlockTag {
		ARMORY,
		RANDOM_POOL,
		RANGED,
		MELEE,
		RELIQUARY,
		ARCANERY,
		DORMITORY,
		NAVIGATIONS,
		PAINTER,
		WALLPAPER,
		HABERDASHER,
		TRAINING,
		CAMPAIGN,
		QUARTERMASTER,
		OUTFITTER,
		NASU,
		MISC,

		ALL,
		CURATED,
		BIRD,
		PVP,
		ARENA,
		MULTIPLAYER,
		SINGLEPLAYER,
		BOSS,
		SANDBOX,
		BOT_COMPLIANT,

		OFFENSE,
		DEFENSE,
		MOBILITY,
		FUEL,
		HEAL,
		ACTIVE_ITEM,
		AMMO,
		WEAPON_DAMAGE,
		PASSIVE_DAMAGE,
		PROJECTILE_MODIFIER,
		GIMMICK,

		MEDIEVAL,
	}
	
	public enum UnlockType {
		EQUIP,
		ARTIFACT,
		ACTIVE,
		CHARACTER,
		COSMETIC,
		LEVEL
	}
}
