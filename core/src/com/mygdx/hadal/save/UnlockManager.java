package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.managers.GameStateManager;

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
	
	public static boolean checkUnlock(Record record, UnlockType type, String name) {
		switch(type) {
		case ACTIVE:
			return record.getUnlockActive().getOrDefault(name, false);
		case ARTIFACT:
			return record.getUnlockArtifact().getOrDefault(name, false);
		case CHARACTER:
			return record.getUnlockCharacter().getOrDefault(name, false);
		case EQUIP:
			return record.getUnlockEquip().getOrDefault(name, false);
		case LEVEL:
			return record.getUnlockLevel().getOrDefault(name, false);
		default:
			return false;
		}
	}
	
	public static void setUnlock(Record record, UnlockType type, String name, boolean unlock) {
		switch(type) {
		case ACTIVE:
			record.getUnlockActive().put(name, unlock);
			break;
		case ARTIFACT:
			record.getUnlockArtifact().put(name, unlock);
			break;
		case CHARACTER:
			record.getUnlockCharacter().put(name, unlock);
			break;
		case EQUIP:
			record.getUnlockEquip().put(name, unlock);
			break;
		case LEVEL:
			record.getUnlockLevel().put(name, unlock);
			break;
		default:
		}
		record.saveRecord();
	}
	
	public enum UnlockTag {
		ARMORY,
		RANDOM_POOL,
		RANGED,
		MELEE,
		RELIQUARY,
		DISPENSARY,
		NAVIGATIONS,
		TRAINING,
		CAMPAIGN,
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
	
	public enum ModTag {
		MISC,
		RANDOM_POOL
	}
}
