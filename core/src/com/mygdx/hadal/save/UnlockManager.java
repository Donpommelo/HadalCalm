package com.mygdx.hadal.save;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class UnlockManager {

	public static void saveUnlocks() {
		Gdx.files.local("save/Unlocks.json").writeString("", false);
		
		Json json = new Json();
		
		HashMap<String, HashMap<String, Boolean>> map = new HashMap<String, HashMap<String, Boolean>>();
		
		HashMap<String, Boolean> equip = new HashMap<String, Boolean>();
		for (UnlockEquip u : UnlockEquip.values()) {
			equip.put(u.name(), u.isUnlocked());
		}
		
		HashMap<String, Boolean> artifacts = new HashMap<String, Boolean>();
		for (UnlockArtifact u : UnlockArtifact.values()) {
			artifacts.put(u.name(), u.isUnlocked());
		}
		
		HashMap<String, Boolean> characters = new HashMap<String, Boolean>();
		for (UnlockCharacter u : UnlockCharacter.values()) {
			characters.put(u.name(), u.isUnlocked());
		}
		
		HashMap<String, Boolean> levels = new HashMap<String, Boolean>();
		for (UnlockLevel u : UnlockLevel.values()) {
			levels.put(u.name(), u.isUnlocked());
		}
		
		map.put("EQUIP", equip);
		map.put("ARTIFACT", artifacts);
		map.put("CHARACTER", characters);
		map.put("LEVEL", levels);
		
		Gdx.files.local("save/Unlocks.json").writeString(json.prettyPrint(map), true);
	}
	
	public static void retrieveUnlocks() {
		JsonReader json;
		JsonValue base;
		
		json = new JsonReader();
		base = json.parse(Gdx.files.internal("save/Unlocks.json"));
		
		for (JsonValue d : base.get("EQUIP")) {
			UnlockEquip.valueOf(d.name()).setUnlocked(d.getBoolean("value"));
		}
		
		for (JsonValue d : base.get("ARTIFACT")) {
			UnlockArtifact.valueOf(d.name()).setUnlocked(d.getBoolean("value"));
		}
		
		for (JsonValue d : base.get("CHARACTER")) {
			UnlockCharacter.valueOf(d.name()).setUnlocked(d.getBoolean("value"));
		}
		
		for (JsonValue d : base.get("LEVEL")) {
			UnlockLevel.valueOf(d.name()).setUnlocked(d.getBoolean("value"));
		}
	}
}
