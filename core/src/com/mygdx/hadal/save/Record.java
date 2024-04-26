package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.hadal.managers.JSONManager;

import static com.mygdx.hadal.managers.JSONManager.JSON;
import static com.mygdx.hadal.managers.JSONManager.READER;

/**
 * A record represents the player's persistent saved data.
 * @author Duthrop Drutanga
 */
public class Record {

	//This is the amount of currency the player has accrued
	private int scrap;
	
	//This is a map of the player's quest flags
	private ObjectMap<String, Integer> flags;
	
	//this is the last ip connected to
	private String lastIp;

	private int saveVersion;

	//This is a map of the player's unlocks
	private ObjectMap<String, Integer> hiScores;
	private ObjectMap<String, Boolean> unlockEquip, unlockArtifact, unlockActive, unlockCharacter, unlockCosmetic, unlockLevel;

	public Record() {}

	public void updateScore(int score, UnlockLevel level) {
		if (hiScores.containsKey(level.toString())) {
			if (score > hiScores.get(level.toString())) {
				hiScores.put(level.toString(), score);
				saveRecord();
			}
		}
	}
	
	/**
	 * This increments the player's scrap, saves, last Ip, etc and saves the record file afterward
	 */
	public void incrementScrap(int amount) {
		scrap = scrap + amount;
		saveRecord();
	}
	
	public void setScrap(int scrap) {
		this.scrap = scrap;
		saveRecord();
	}
	
	public void setlastIp(String lastIp) {
		this.lastIp = lastIp;
		saveRecord();
	}

	/**
	 * This simple saves the record in a designated file
	 */
	public void saveRecord() {
		Gdx.files.local("save/Records.json").writeString(JSON.prettyPrint(this), false);
	}
	
	/**
	 * a new record is created if no valid record is found
	 * This new record has default values for all fields
	 */
	public static void createNewRecord() {
		Record newRecord = new Record();
		newRecord.scrap = 0;
		
		newRecord.flags = new ObjectMap<>();
		newRecord.flags.put("ARTIFACTSLOT1", 1);
		newRecord.flags.put("ARTIFACTSLOT2", 1);
		newRecord.flags.put("ARTIFACTSLOT3", 1);
		newRecord.flags.put("ARTIFACTSLOT4", 1);
		newRecord.flags.put("ARTIFACTSLOT5", 0);
		newRecord.flags.put("HUB_REACHED", 0);
		newRecord.flags.put("DIALOG1", 0);
		newRecord.flags.put("WRECK0SC", 0);
		newRecord.flags.put("WRECK1SC", 0);
		newRecord.flags.put("DERELICTTA1", 0);
		newRecord.flags.put("DERELICTTA2", 0);
		newRecord.flags.put("DERELICTTB1", 0);
		newRecord.flags.put("DERELICTTB2", 0);
		newRecord.flags.put("DERELICTTB3", 0);
		newRecord.flags.put("DERELICTTB4", 0);
		newRecord.flags.put("DERELICTTB5", 0);
		newRecord.flags.put("DERELICTTB6", 0);
		newRecord.flags.put("CACHE1", 0);
		newRecord.flags.put("CACHE2", 0);
		newRecord.flags.put("CACHE3", 0);
		newRecord.flags.put("CACHE4", 0);
		newRecord.flags.put("CACHE5", 0);
		newRecord.flags.put("CACHE6", 0);
		newRecord.flags.put("PLENUMTURBINE", 0);
		newRecord.flags.put("PLENUM0SC", 0);
		newRecord.flags.put("PLENUM1SC", 0);
		newRecord.flags.put("SLUICE0SC", 0);
		newRecord.flags.put("SLUICE1SC", 0);
		newRecord.flags.put("SLUICE2SC", 0);
		newRecord.flags.put("SLUICEFINALSC", 0);
		newRecord.flags.put("BOSS1", 0);
		newRecord.flags.put("PARTY", 0);
		
		newRecord.hiScores = new ObjectMap<>();
		newRecord.unlockEquip = new ObjectMap<>();
		newRecord.unlockArtifact = new ObjectMap<>();
		newRecord.unlockActive = new ObjectMap<>();
		newRecord.unlockCharacter = new ObjectMap<>();
		newRecord.unlockCosmetic = new ObjectMap<>();
		newRecord.unlockLevel = new ObjectMap<>();

		for (UnlockEquip equip : UnlockEquip.values()) {
			newRecord.unlockEquip.put(equip.toString(), false);
		}
		
		for (UnlockArtifact artifact : UnlockArtifact.values()) {
			newRecord.unlockArtifact.put(artifact.toString(), false);
		}
		
		for (UnlockActives active : UnlockActives.values()) {
			newRecord.unlockActive.put(active.toString(), false);
		}
		
		for (UnlockCharacter character : UnlockCharacter.values()) {
			newRecord.unlockCharacter.put(character.toString(), true);
		}

		for (UnlockCosmetic cosmetic : UnlockCosmetic.values()) {
			newRecord.unlockCosmetic.put(cosmetic.toString(), true);
		}

		for (UnlockLevel level : UnlockLevel.values()) {
			newRecord.unlockLevel.put(level.toString(), false);
		}

		newRecord.hiScores.put("DM_BROUHAHA", 0);
		newRecord.hiScores.put("DM_FLOTSAM", 0);
		newRecord.hiScores.put("DM_HORIZON", 0);
		newRecord.hiScores.put("DM_LAGAN", 0);
		newRecord.hiScores.put("DM_THRESHOLD", 0);
		newRecord.hiScores.put("DM_PILE", 0);
		newRecord.hiScores.put("DM_WARP", 0);
		newRecord.hiScores.put("DM_WETWORKS", 0);
		newRecord.hiScores.put("DM_ZIGGURAT", 0);

		newRecord.lastIp = "";
		newRecord.saveVersion = JSONManager.SAVE_VERSION;

		Gdx.files.local("save/Records.json").writeString(JSON.prettyPrint(newRecord), false);
	}

	/**
	 * This retrieves the player's records at the start of the game
	 * @return the player's records (or a default record if file is missing or malformed)
	 */
	public static Record retrieveRecord() {
		Record tempRecord;
		try {
			tempRecord = JSON.fromJson(Record.class, READER.parse(Gdx.files.local("save/Records.json")).toJson(JsonWriter.OutputType.json));
		} catch (SerializationException e) {
			Record.createNewRecord();
			tempRecord = JSON.fromJson(Record.class, READER.parse(Gdx.files.local("save/Records.json")).toJson(JsonWriter.OutputType.json));
		}
		return tempRecord;
	}

	public int getScrap() { return scrap; }
	
	public String getLastIp() { return lastIp; }
	
	/**
	 * This acquires the number of single-player-mode artifact slots based on quest completion
	 */
	public int getSlotsUnlocked() { 
		
		int slots = 0;
		
		if (flags.get("ARTIFACTSLOT1") == 1) {
			slots++;
		}
		if (flags.get("ARTIFACTSLOT2") == 1) {
			slots++;
		}
		if (flags.get("ARTIFACTSLOT3") == 1) {
			slots++;
		}
		if (flags.get("ARTIFACTSLOT4") == 1) {
			slots++;
		}
		if (flags.get("ARTIFACTSLOT5") == 1) {
			slots++;
		}
		return slots; 
	}
	
	public ObjectMap<String, Integer> getFlags() { return flags; }

	public ObjectMap<String, Integer> getHiScores() { return hiScores; }
	
	public ObjectMap<String, Boolean> getUnlockEquip() { return unlockEquip; }

	public ObjectMap<String, Boolean> getUnlockArtifact() { return unlockArtifact; }

	public ObjectMap<String, Boolean> getUnlockActive() {	return unlockActive; }

	public ObjectMap<String, Boolean> getUnlockCharacter() { return unlockCharacter; }

	public ObjectMap<String, Boolean> getUnlockCosmetic() { return unlockCosmetic; }

	public ObjectMap<String, Boolean> getUnlockLevel() { return unlockLevel; }

	public int getSaveVersion() { return saveVersion; }
}
