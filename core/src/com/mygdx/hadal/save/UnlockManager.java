package com.mygdx.hadal.save;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class UnlockManager {
	
	public static void saveUnlocks(int type) {

		Json json = new Json();
		String filename = "";
		String save = "";
		switch(type) {
		case 0:
			filename = "save/Equips.json";
			
			HashMap<String, InfoEquip> equip = new HashMap<String, InfoEquip>();
			
			for (UnlockEquip u : UnlockEquip.values()) {
				if (u.getInfo() != null) {
					equip.put(u.name(), u.getInfo());
				}
			}
			save = json.prettyPrint(equip);
			
			break;
		case 1:
			filename = "save/Artifacts.json";
			
			HashMap<String, InfoArtifact> artifact = new HashMap<String, InfoArtifact>();
			
			for (UnlockArtifact u : UnlockArtifact.values()) {
				if (u.getInfo() != null) {
					artifact.put(u.name(), u.getInfo());
				}
			}
			save = json.prettyPrint(artifact);
			
			break;
		case 2:
			filename = "save/Characters.json";
			
			HashMap<String, InfoCharacter> character = new HashMap<String, InfoCharacter>();
			
			for (UnlockCharacter u : UnlockCharacter.values()) {
				if (u.getInfo() != null) {
					character.put(u.name(), u.getInfo());
				}
			}
			save = json.prettyPrint(character);
				
			break;
		case 3:
			filename = "save/Levels.json";
			
			HashMap<String, InfoLevel> level = new HashMap<String, InfoLevel>();
			
			for (UnlockLevel u : UnlockLevel.values()) {
				if (u.getInfo() != null) {
					level.put(u.name(), u.getInfo());
				}
				
			}
			save = json.prettyPrint(level);
			
			break;
		}
		
		Gdx.files.local(filename).writeString("", false);
		Gdx.files.local(filename).writeString(save, true);
	}
	
	public static void retrieveUnlocks() {
		JsonReader reader;
		
		reader = new JsonReader();
		
		Json json = new Json();
		
		for (JsonValue d : reader.parse(Gdx.files.internal("save/Equips.json"))) {
			UnlockEquip.valueOf(d.name()).setInfo(json.fromJson(InfoEquip.class, d.toJson(OutputType.minimal)));
		}
		
		for (JsonValue d : reader.parse(Gdx.files.internal("save/Artifacts.json"))) {
			UnlockArtifact.valueOf(d.name()).setInfo(json.fromJson(InfoArtifact.class, d.toJson(OutputType.minimal)));
		}
		
		for (JsonValue d : reader.parse(Gdx.files.internal("save/Characters.json"))) {
			UnlockCharacter.valueOf(d.name()).setInfo(json.fromJson(InfoCharacter.class, d.toJson(OutputType.minimal)));
		}
		
		for (JsonValue d : reader.parse(Gdx.files.internal("save/Levels.json"))) {
			UnlockLevel.valueOf(d.name()).setInfo(json.fromJson(InfoLevel.class, d.toJson(OutputType.minimal)));
		}
	}	
	
	public enum UnlockTag {
		ARMORY,
		RANDOM_POOL,
		RANGED,
		MELEE,
		RELIQUARY,
		NAVIGATIONS,
		TRAINING,
		CAMPAIGN,
		ARENA,
		NASU,
		MISC
	}
}
