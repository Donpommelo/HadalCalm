package com.mygdx.hadal.save;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.managers.GameStateManager;

public class UnlockManager {
	
	public static void saveUnlocks(int type) {

		String filename = "";
		String save = "";
		switch(type) {
		case 0:
			filename = "save/Equips.json";
			
			HashMap<String, InfoItem> equip = new HashMap<String, InfoItem>();
			
			for (UnlockEquip u : UnlockEquip.values()) {
				if (u.getInfo() != null) {
					equip.put(u.name(), u.getInfo());
				}
			}
			save = GameStateManager.json.prettyPrint(equip);
			
			break;
		case 1:
			filename = "save/Artifacts.json";
			
			HashMap<String, InfoItem> artifact = new HashMap<String, InfoItem>();
			
			for (UnlockArtifact u : UnlockArtifact.values()) {
				if (u.getInfo() != null) {
					artifact.put(u.name(), u.getInfo());
				}
			}
			save = GameStateManager.json.prettyPrint(artifact);
			
			break;
		case 2:
			filename = "save/Characters.json";
			
			HashMap<String, InfoCharacter> character = new HashMap<String, InfoCharacter>();
			
			for (UnlockCharacter u : UnlockCharacter.values()) {
				if (u.getInfo() != null) {
					character.put(u.name(), u.getInfo());
				}
			}
			save = GameStateManager.json.prettyPrint(character);
				
			break;
		case 3:
			filename = "save/Levels.json";
			
			HashMap<String, InfoLevel> level = new HashMap<String, InfoLevel>();
			
			for (UnlockLevel u : UnlockLevel.values()) {
				if (u.getInfo() != null) {
					level.put(u.name(), u.getInfo());
				}
				
			}
			save = GameStateManager.json.prettyPrint(level);
			
			break;
		case 4:
			filename = "save/Actives.json";
			
			HashMap<String, InfoItem> active = new HashMap<String, InfoItem>();
			
			for (UnlockActives u : UnlockActives.values()) {
				if (u.getInfo() != null) {
					active.put(u.name(), u.getInfo());
				}
				
			}
			save = GameStateManager.json.prettyPrint(active);
			
			break;
		}
		
		Gdx.files.local(filename).writeString("", false);
		Gdx.files.local(filename).writeString(save, true);
	}
	
	public static void retrieveUnlocks() {
		
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
			UnlockCharacter.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoCharacter.class, d.toJson(OutputType.minimal)));
		}
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Levels.json"))) {
			UnlockLevel.valueOf(d.name()).setInfo(GameStateManager.json.fromJson(InfoLevel.class, d.toJson(OutputType.minimal)));
		}
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
		MISC
	}
	
	public enum ModTag {
		MISC,
		RANDOM_POOL
	}
}
