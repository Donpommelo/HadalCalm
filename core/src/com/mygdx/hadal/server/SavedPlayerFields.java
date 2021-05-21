package com.mygdx.hadal.server;

/**
 * This represents saved fields for a player.
 * @author Medelaire Morcester
 */
public class SavedPlayerFields {
	
	//Player's name
	private String name;
	
	//Player's stored stats
	private int wins, kills, deaths, score, teamScore, lives, ping, connID;

	//did the player win their last game? This is used to let the client know who won in the results screen.
	private boolean wonLast;
	
	//this unused constructor is needed by kryo for serialization
	public SavedPlayerFields() {}
	
	public SavedPlayerFields(String name, int connID) {
		this.name = name;
		this.connID = connID;
	}
	
	public String getKD() {
		return kills + "/" + deaths;
	}
	
	/**
	 * This is called when this player kills another player
	 */
	public void registerKill(boolean incrementScore) {
		kills++;
		if (incrementScore) {
			score++;
		}
	}
	
	/**
	 * This is called when this player dies
	 */
	public void registerDeath() {
		deaths++;
		lives--;
	}

	/**
	 * Upon a new level, reset stats
	 */
	public void newLevelReset() {
		this.kills = 0;
		this.deaths = 0;
		this.score = 0;
		this.teamScore = 0;
		this.lives = 1;
		wonLast = false;
	}
	
	public void win() {
		wins++;
		wonLast = true;
	}

	//this gets the name displayed in score windows. Gives an indication of which player is the host
	public String getName() { 
		if (connID == 0) {
			return "(HOST) " + name; 
		} else {
			return "(" + ping + " ms) " + name; 
		}
	}
	
	public String getNameShort() { 
		return name;
	}

	/**
	 * This abridges a name according to a max length in characters
	 * @param includePing: should we include the player's ping in the name?
	 * @param maxNameLen: the max characters
	 * @return the new abridged name
	 */
	public String getNameAbridged(boolean includePing, int maxNameLen) {
		String displayedName = getNameShort();
		if (includePing) {
			displayedName = getName();
		}
		
		if (displayedName.length() > maxNameLen) {
			displayedName = displayedName.substring(0, maxNameLen).concat("...");
		}
		
		return displayedName;
	}

	public int getConnID() { return connID; }

	public boolean isWonLast() { return wonLast; }

	public void setWonLast(boolean wonLast) { this.wonLast = wonLast; }

	public int getWins() { return wins; }

	public void setWins(int wins) { this.wins = wins; }

	public int getKills() { return kills; }

	public void setKills(int kills) { this.kills = kills; }

	public int getDeaths() { return deaths; }

	public void setDeaths(int deaths) { this.deaths = deaths; }

	public int getScore() {	return score; }

	public void setScore(int score) { this.score = score; }

	public int getTeamScore() {	return teamScore; }

	public void setTeamScore(int teamScore) { this.teamScore = teamScore; }

	public int getLives() { return lives; }

	public void setLives(int lives) { this.lives = lives; }

	public int getPing() { return ping; }

	public void setPing(int ping) { this.ping = ping; }
}
