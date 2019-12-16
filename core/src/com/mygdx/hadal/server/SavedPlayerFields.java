package com.mygdx.hadal.server;

/**
 * This represents saved fields for a player.
 * @author Zachary Tu
 *
 */
public class SavedPlayerFields {
	
	//Player's name
	private String name;
	
	//Player's stored stats
	private int wins, kills, deaths, killStreak, deathStreak, score;
	private boolean alive;
	
	public SavedPlayerFields() {
		this("");
	}
	
	public SavedPlayerFields(String name) {
		this.name = name;
		this.wins = 0;
		this.kills = 0;
		this.deaths = 0;
		this.killStreak = 0;
		this.deathStreak = 0;
		this.score = 0;
		
		this.alive = true;
	}
	
	public String getKD() {
		return kills + "/" + deaths;
	}
	
	/**
	 * This is called when this player kills another player
	 */
	public void registerKill() {
		kills++;
		killStreak++;
		score++;
		
		deathStreak = 0;
	}
	
	/**
	 * This is called when this player dies
	 */
	public void registerDeath() {
		deaths++;
		deathStreak++;
		score--;
		
		killStreak = 0;
		
		alive = false;
	}

	/**
	 * Upon a new level, reset stats
	 */
	public void newLevelReset() {
		this.kills = 0;
		this.deaths = 0;
		this.killStreak = 0;
		this.deathStreak = 0;
		this.score = 0;
		
		alive = true;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWins() {
		return wins;
	}

	public void getWin() {
		wins++;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getKillStreak() {
		return killStreak;
	}

	public void setKillStreak(int killStreak) {
		this.killStreak = killStreak;
	}

	public int getDeathStreak() {
		return deathStreak;
	}

	public void setDeathStreak(int deathStreak) {
		this.deathStreak = deathStreak;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
