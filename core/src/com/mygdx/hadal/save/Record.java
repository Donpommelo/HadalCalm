package com.mygdx.hadal.save;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A record represents the player's persistent saved data.
 * @author Zachary Tu
 *
 */
public class Record {

	//This is the amount of currency the player has accrued
	private int scrap;
	
	//This is the timer used for pvp matches
	private int timer;
		
	//This is the player's current loadout that they will start with
	private String[] equips;
	private String[] artifacts;
	private String active, character;
	
	//This is the player's starting name
	private String name;
	
	//This is a map of the player's quest flags
	private Map<String, Integer> flags;
	
	//This is a map of the player's high scores for each map
	private Map<String, Integer> hiScores;	
	
	//This is a map of the player's unlocks
	private Map<String, Boolean> unlockEquip;
	private Map<String, Boolean> unlockArtifact;
	private Map<String, Boolean> unlockActive;
	private Map<String, Boolean> unlockCharacter;
	private Map<String, Boolean> unlockLevel;
	
	private static final int maxNameLength = 100;
	
	public Record() {}
	
	/**
	 * This increments the player's scrap and saves
	 * @param amount
	 */
	public void incrementScrap(int amount) {
		scrap = scrap + amount;
		saveRecord();
	}
	
	public void setTimer(int timer) {
		this.timer = timer;
		saveRecord();
	}
	
	/**
	 * This updates a player's high score for a level
	 * @param score: the new score
	 * @param level: the level
	 * @return: was the new score a high score?
	 */
	public boolean updateScore(int score, UnlockLevel level) {
		if (score > hiScores.get(level.toString())) {
			hiScores.put(level.toString(), score);
			
			saveRecord();
			return true;
		}
		return false;
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
		
		newRecord.flags = new HashMap<String, Integer>();
		newRecord.flags.put("INTRO", 0);
		newRecord.flags.put("TUTORIAL", 0);
		newRecord.equips = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newRecord.artifacts = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newRecord.active = "NOTHING";
		newRecord.character = "MOREAU";
		newRecord.name = "";
		
		newRecord.unlockEquip = new HashMap<String, Boolean>();
		newRecord.unlockArtifact = new HashMap<String, Boolean>();
		newRecord.unlockActive = new HashMap<String, Boolean>();
		newRecord.unlockCharacter = new HashMap<String, Boolean>();
		newRecord.unlockLevel = new HashMap<String, Boolean>();
		newRecord.hiScores = new HashMap<String, Integer>();
		
		for (UnlockEquip equip: UnlockEquip.values()) {
			newRecord.unlockEquip.put(equip.toString(), true);
		}
		
		for (UnlockArtifact artifact: UnlockArtifact.values()) {
			newRecord.unlockArtifact.put(artifact.toString(), true);
		}
		
		for (UnlockActives active: UnlockActives.values()) {
			newRecord.unlockActive.put(active.toString(), true);
		}
		
		for (UnlockCharacter character: UnlockCharacter.values()) {
			newRecord.unlockCharacter.put(character.toString(), true);
		}
		
		for (UnlockLevel level: UnlockLevel.values()) {
			newRecord.unlockLevel.put(level.toString(), true);
			newRecord.hiScores.put(level.toString(), 0);
		}
		
		Gdx.files.local("save/Records.json").writeString(GameStateManager.json.prettyPrint(newRecord), false);
	}
	
	public void setEquips(int index, String equip) {
		this.equips[index] = equip;
		saveRecord();
	}
	
	public void setArtifact(int index, String artifact) {
		this.artifacts[index] = artifact;
		saveRecord();
	}
	
	public void setActive(String active) {
		this.active = active;
		saveRecord();
	}
	
	public void setCharacter(String character) {
		this.character = character;
		saveRecord();
	}
	
	public void setName(String name) {
		this.name = name.substring(0, Math.min(name.length(), maxNameLength));
		saveRecord();
	}
	
	public int getScrap() { return scrap; }

	public int getTimer() { return timer; }
	
	public Map<String, Integer> getHiScores() {	return hiScores; }

	public Map<String, Integer> getFlags() { return flags; }

	public String[] getEquips() {return equips;}

	public String[] getArtifacts() { return artifacts; }

	public String getActive() {	return active; }

	public String getCharacter() { return character; }

	public String getName() { return name.substring(0, Math.min(name.length(), maxNameLength)); }

	public Map<String, Boolean> getUnlockEquip() { return unlockEquip; }

	public Map<String, Boolean> getUnlockArtifact() { return unlockArtifact; }

	public Map<String, Boolean> getUnlockActive() {	return unlockActive; }

	public Map<String, Boolean> getUnlockCharacter() { return unlockCharacter; }
	
	public Map<String, Boolean> getUnlockLevel() { return unlockLevel; }	
}
