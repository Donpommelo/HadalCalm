package com.mygdx.hadal.save;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class Record {

	private int scrap;
	private int scrip;
	
	private Map<String, Integer> hiScores;
	private Map<String, Integer> flags;
	
	private String[] equips;
	private String artifact, active, character;
	
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
		Json json = new Json();
		Gdx.files.local("save/Records.json").writeString(json.prettyPrint(this), false);
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
	
	
}
