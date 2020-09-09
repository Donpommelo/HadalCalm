package com.mygdx.hadal.server;

import com.mygdx.hadal.states.PlayState;

/**
 * This represents saved fields for a player.
 * @author Zachary Tu
 */
public class SavedPlayerFields {
	
	//Player's name
	private String name;
	
	//Player's stored stats
	private int wins, kills, deaths, score, lives;
	
	//is this player the host?
	private boolean host;

	//this unused constructor is needed by kryo for serialization
	public SavedPlayerFields() {}
	
	public SavedPlayerFields(String name, boolean host) {
		this.name = name;
		this.host = host;
	}
	
	public String getKD() {
		return kills + "/" + deaths;
	}
	
	/**
	 * This is called when this player kills another player
	 */
	public void registerKill() {
		kills++;
		score++;
	}
	
	/**
	 * This is called when this player dies
	 */
	public void registerDeath() {
		deaths++;
		score--;
		lives--;
	}

	/**
	 * Upon a new level, reset stats
	 */
	public void newLevelReset(PlayState state) {
		this.kills = 0;
		this.deaths = 0;
		this.score = 0;
		this.lives = 1;
	}
	
	public void win() { wins++; }

	//this gets the name displayed in score windows. Gives an indication of which player is the host
	public String getName() { 
		if (host) {
			return "(HOST) " + name; 
		} else {
			return name; 
		}
	}

	public boolean isHost() { return host; }
	
	public int getWins() { return wins; }

	public int getKills() { return kills; }

	public int getDeaths() { return deaths; }

	public int getScore() {	return score; }

	public void setScore(int score) { this.score = score; }
	
	public int getLives() { return lives; }

	public void setLives(int lives) { this.lives = lives; }
}
