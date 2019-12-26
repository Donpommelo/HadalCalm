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
	
	//This is a map of the player's high scores for each map
	private Map<String, Integer> hiScores;
	
	//This is a map of the player's quest flags
	private Map<String, Integer> flags;
	
	//This is the player's current loadout that they will start with
	private String[] equips;
	private String[] artifacts;
	private String active, character;
	
	//This is the player's starting name
	private String name;
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
	
	/**
	 * This updates a player's high score for a level
	 * @param score: the new score
	 * @param level: the level
	 * @return: was the new score a high score?
	 */
	public boolean updateScore(int score, UnlockLevel level) {
		if (score > hiScores.get(level.getName())) {
			hiScores.put(level.getName(), score);
			
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
		newRecord.hiScores = new HashMap<String, Integer>();
		
		for (UnlockLevel level: UnlockLevel.values()) {
			newRecord.hiScores.put(level.toString(), 0);
		}
		
		newRecord.flags = new HashMap<String, Integer>();
		newRecord.flags.put("INTRO", 0);
		newRecord.flags.put("TUTORIAL", 0);
		newRecord.equips = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newRecord.artifacts = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newRecord.active = "NOTHING";
		newRecord.character = "MOREAU";
		newRecord.name = "";
		
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

	public Map<String, Integer> getHiScores() {	return hiScores; }

	public Map<String, Integer> getFlags() { return flags; }

	public String[] getEquips() {return equips;}

	public String[] getArtifacts() { return artifacts; }

	public String getActive() {	return active; }

	public String getCharacter() { return character; }

	public String getName() { return name.substring(0, Math.min(name.length(), maxNameLength)); }
}
