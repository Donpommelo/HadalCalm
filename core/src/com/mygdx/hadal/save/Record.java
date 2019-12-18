package com.mygdx.hadal.save;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.managers.GameStateManager;

public class Record {

	private int scrap;
	private int scrip;
	
	private Map<String, Integer> hiScores;
	private Map<String, Integer> flags;
	
	private String[] equips;
	private String artifact, active, character;
	
	private String name;
	
	public Record() {
		
	}
	
	public void incrementScrap(int amount) {
		scrip = scrap + amount;
		saveRecord();
	}
	
	public void incrementScrip(int amount) {
		scrip = scrip + amount;
		saveRecord();
	}
	
	public boolean updateScore(int score, String level) {
		if (score > hiScores.get(level)) {
			hiScores.put(level, score);
			
			saveRecord();
			return true;
		}
		return false;
	}
	
	public void saveRecord() {
		Gdx.files.local("save/Records.json").writeString(GameStateManager.json.prettyPrint(this), false);
	}
	
	public static void createNewRecord() {
		Record newRecord = new Record();
		newRecord.scrap = 0;
		newRecord.scrip = 0;
		newRecord.hiScores = new HashMap<String, Integer>();
		
		for (UnlockLevel level: UnlockLevel.values()) {
			newRecord.hiScores.put(level.toString(), 0);
		}
		
		newRecord.flags = new HashMap<String, Integer>();
		newRecord.flags.put("INTRO", 0);
		newRecord.flags.put("TUTORIAL", 0);
		newRecord.equips = new String[] {"NOTHING", "NOTHING", "NOTHING"};
		newRecord.artifact = "NOTHING";
		newRecord.active = "NOTHING";
		newRecord.character = "MOREAU";
		newRecord.name = "";
		
		Gdx.files.local("save/Records.json").writeString(GameStateManager.json.prettyPrint(newRecord), false);
	}
	
	public int getScrap() {
		return scrap;
	}

	public int getScrip() {
		return scrip;
	}

	public Map<String, Integer> getHiScores() {
		return hiScores;
	}

	public Map<String, Integer> getFlags() {
		return flags;
	}

	public String[] getEquips() {
		return equips;
	}

	public void setEquips(int index, String equip) {
		this.equips[index] = equip;
		saveRecord();
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
		saveRecord();
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
		saveRecord();
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
		saveRecord();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		saveRecord();
	}
}
