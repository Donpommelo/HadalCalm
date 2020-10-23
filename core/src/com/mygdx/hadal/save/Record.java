package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.managers.GameStateManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A record represents the player's persistent saved data.
 * @author Duthrop Drutanga
 */
public class Record {

	//This is the amount of currency the player has accrued
	private int scrap;
	
	//This is a map of the player's quest flags
	private Map<String, Integer> flags;
	
	//this is the last ip connected to
	private String lastIp;
	
	//This is a map of the player's unlocks
	private Map<String, Integer> hiScores;
	private Map<String, Boolean> unlockEquip;
	private Map<String, Boolean> unlockArtifact;
	private Map<String, Boolean> unlockActive;
	private Map<String, Boolean> unlockCharacter;
	private Map<String, Boolean> unlockLevel;
	
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
		Gdx.files.local("save/Records.json").writeString(GameStateManager.json.prettyPrint(this), false);
	}
	
	/**
	 * a new record is created if no valid record is found
	 * This new record has default values for all fields
	 */
	public static void createNewRecord() {
		Record newRecord = new Record();
		newRecord.scrap = 0;
		
		newRecord.flags = new HashMap<>();
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
		
		newRecord.hiScores = new HashMap<>();
		newRecord.unlockEquip = new HashMap<>();
		newRecord.unlockArtifact = new HashMap<>();
		newRecord.unlockActive = new HashMap<>();
		newRecord.unlockCharacter = new HashMap<>();
		newRecord.unlockLevel = new HashMap<>();
		
		for (UnlockEquip equip: UnlockEquip.values()) {
			newRecord.unlockEquip.put(equip.toString(), false);
		}
		
		for (UnlockArtifact artifact: UnlockArtifact.values()) {
			newRecord.unlockArtifact.put(artifact.toString(), false);
		}
		
		for (UnlockActives active: UnlockActives.values()) {
			newRecord.unlockActive.put(active.toString(), false);
		}
		
		for (UnlockCharacter character: UnlockCharacter.values()) {
			newRecord.unlockCharacter.put(character.toString(), true);
		}
		
		for (UnlockLevel level: UnlockLevel.values()) {
			newRecord.unlockLevel.put(level.toString(), false);
		}
		newRecord.hiScores.put("ARENA_FLOTSAM", 0);
		newRecord.hiScores.put("ARENA_HORIZON", 0);
		newRecord.hiScores.put("ARENA_LAGAN", 0);
		newRecord.hiScores.put("ARENA_PILE", 0);
		newRecord.hiScores.put("ARENA_WARP", 0);
		newRecord.hiScores.put("ARENA_WETWORKS", 0);
		newRecord.hiScores.put("ARENA_ZIGGURAT", 0);

		newRecord.lastIp = "";

		Gdx.files.local("save/Records.json").writeString(GameStateManager.json.prettyPrint(newRecord), false);
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
	
	public Map<String, Integer> getFlags() { return flags; }

	public Map<String, Integer> getHiScores() { return hiScores; }
	
	public Map<String, Boolean> getUnlockEquip() { return unlockEquip; }

	public Map<String, Boolean> getUnlockArtifact() { return unlockArtifact; }

	public Map<String, Boolean> getUnlockActive() {	return unlockActive; }

	public Map<String, Boolean> getUnlockCharacter() { return unlockCharacter; }
	
	public Map<String, Boolean> getUnlockLevel() { return unlockLevel; }	
}
