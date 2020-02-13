package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * The UnlockManager manages the player's unlocked weapons, artifacts, etc
 * @author Zachary Tu
 *
 */
public class UnlockManager {
	
	/**
	 * This retrives the player's unlocks from a file
	 */
	public static void retrieveItemInfo() {
				
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Equips.json"))) {
			UnlockEquip.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.minimal)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Artifacts.json"))) {
			UnlockArtifact.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.minimal)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Actives.json"))) {
			UnlockActives.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.minimal)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Characters.json"))) {
			UnlockCharacter.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.minimal)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Levels.json"))) {
			UnlockLevel.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoItem.class, d.toJson(OutputType.minimal)));
		}
	}	
	
	public static InfoItem getInfo(UnlockType type, String name) {
		switch(type) {
		case ACTIVE:
			return UnlockActives.valueOf(name).getInfo();
		case ARTIFACT:
			return UnlockArtifact.valueOf(name).getInfo();
		case CHARACTER:
			return UnlockCharacter.valueOf(name).getInfo();
		case EQUIP:
			return UnlockEquip.valueOf(name).getInfo();
		case LEVEL:
			return UnlockLevel.valueOf(name).getInfo();
		default:
			return null;
		}
	}
	
	public static boolean checkTags(InfoItem item, ArrayList<UnlockTag> tags) {
		
		for (int i = 0; i < tags.size(); i++) {
			
			boolean tagPresent = false;
			
			if (item == null) {
				return false;
			}
			
			for (int j = 0; j < item.getTags().size(); j++) {
				if (tags.get(i).equals(item.getTags().get(j))) {
					tagPresent = true;
				}
			}
			if (!tagPresent) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkUnlock(PlayState state, UnlockType type, String name) {
		switch(type) {
		case ACTIVE:
			return state.getGsm().getRecord().getUnlockActive().getOrDefault(name, false);
		case ARTIFACT:
			return state.getGsm().getRecord().getUnlockArtifact().getOrDefault(name, false);
		case CHARACTER:
			return state.getGsm().getRecord().getUnlockCharacter().getOrDefault(name, false);
		case EQUIP:
			return state.getGsm().getRecord().getUnlockEquip().getOrDefault(name, false);
		case LEVEL:
			return state.getGsm().getRecord().getUnlockLevel().getOrDefault(name, false);
		default:
			return false;
		}
	}
	
	public static void setUnlock(PlayState state, UnlockType type, String name, boolean unlock) {
		switch(type) {
		case ACTIVE:
			state.getGsm().getRecord().getUnlockActive().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", "UNLOCKED ACTIVE: " + getInfo(type, name).getName(), "", true, true, true, 3.0f, null, null);
			}
			break;
		case ARTIFACT:
			state.getGsm().getRecord().getUnlockArtifact().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", "UNLOCKED ARTIFACT: " + getInfo(type, name).getName(), "", true, true, true, 3.0f, null, null);
			}
			break;
		case CHARACTER:
			state.getGsm().getRecord().getUnlockCharacter().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", "UNLOCKED CHARACTER: " + getInfo(type, name).getName(), "", true, true, true, 3.0f, null, null);
			}
			break;
		case EQUIP:
			state.getGsm().getRecord().getUnlockEquip().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", "UNLOCKED EQUIP: " + getInfo(type, name).getName(), "", true, true, true, 3.0f, null, null);
			}
			break;
		case LEVEL:
			state.getGsm().getRecord().getUnlockLevel().put(name, unlock);
			if (unlock) {
				state.getDialogBox().addDialogue("", "UNLOCKED LEVEL: " + getInfo(type, name).getName(), "", true, true, true, 3.0f, null, null);
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
		DISPENSARY,
		DORMITORY,
		NAVIGATIONS,
		TRAINING,
		CAMPAIGN,
		QUARTERMASTER,
		ARENA,
		NASU,
		MISC,
		PVP,
	}
	
	public enum UnlockType {
		EQUIP,
		ARTIFACT,
		ACTIVE,
		CHARACTER,
		LEVEL
	}
}
