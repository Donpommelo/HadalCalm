package com.mygdx.hadal.server;

import com.mygdx.hadal.states.PlayState;

/**
 * This represents saved fields for a player.
 * @author Zachary Tu
 *
 */
public class SavedPlayerFields {
	
	//Player's name
	private String name;
	
	//Player's stored stats
	private int wins, kills, deaths, score, lives;
	
	public SavedPlayerFields() {
		this("");
	}
	
	public SavedPlayerFields(String name) {
		this.name = name;
		this.wins = 0;
		this.kills = 0;
		this.deaths = 0;
		this.score = 0;
		this.lives = 0;
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
	
	public String getName() { return name; }

	public int getWins() { return wins; }

	public void win() { wins++; }

	public int getKills() { return kills; }

	public int getDeaths() { return deaths; }

	public int getScore() {	return score; }

	public void setScore(int score) { this.score = score; }
	
	public int getLives() { return lives; }

	public void setLives(int lives) { this.lives = lives; }
}
