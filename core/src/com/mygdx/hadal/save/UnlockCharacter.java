package com.mygdx.hadal.save;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockEquip.UnlockType;

public enum UnlockCharacter {

	MOREAU(AssetList.PLAYER_MOREAU_ATL.toString(), "Moreau"),
	TELEMACHUS(AssetList.PLAYER_TELE_ATL.toString(), "Telemachus"),
	TAKANORI(AssetList.PLAYER_TAKA_ATL.toString(), "Moreau"),
	;
	private String sprite, name;
	private boolean unlocked;
	
	UnlockCharacter(String sprite, String name) {
		this.sprite = sprite;
		this.name= name;
		this.unlocked = false;
	}

	public static Array<String> getUnlocks(UnlockType type) {
		Array<String> items = new Array<String>();
		
		for (UnlockCharacter u : UnlockCharacter.values()) {
			if (u.isUnlocked()) {
				items.add(u.getSprite());
			}
		}
		
		return items;
	}
	
	public static void retrieveUnlocks() {
		JsonReader json;
		JsonValue base;
		
		json = new JsonReader();
		base = json.parse(Gdx.files.internal("save/Unlocks.json"));
		
		for (JsonValue d : base) {
			valueOf(d.name()).setUnlocked(d.getBoolean("value"));
		}
	}
	
	public static void saveUnlocks() {
		Gdx.files.local("save/Unlocks.json").writeString("", false);
		
		Json json = new Json();
		
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		
		for (UnlockCharacter u : UnlockCharacter.values()) {
			map.put(u.name(), u.unlocked);
		}
		
		Gdx.files.local("save/Unlocks.json").writeString(json.toJson(map), true);
	}
	
	public String getSprite() {
		return sprite;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
}
