package com.mygdx.hadal.server;

/**
 * This represents saved fields for a player.
 * @author Medelaire Morcester
 */
public class SavedPlayerFields {
	
	//Player's name
	private String name;
	
	//Player's stored stats
	private int wins, kills, deaths, assists, score, teamScore, extraModeScore, ping, connID;
	private int lives = 1;

	//did the player win their last game? This is used to let the client know who won in the results screen.
	private boolean wonLast;
	
	//this unused constructor is needed by kryo for serialization
	public SavedPlayerFields() {}
	
	public SavedPlayerFields(String name, int connID) {
		this.name = name;
		this.connID = connID;
	}

	/**
	 * Upon a new level, reset stats
	 */
	public void newLevelReset() {
		this.kills = 0;
		this.deaths = 0;
		this.assists = 0;
		this.score = 0;
		this.teamScore = 0;
		this.extraModeScore = 0;
		this.lives = 1;
		wonLast = false;
	}
	
	public void win() {
		wins++;
		wonLast = true;
	}

	//this gets the name displayed in score windows. Gives an indication of which player is the host
	public String getPingText() {
		if (connID == 0) {
			return "(HOST) ";
		} else if (connID < 0) {
			return "(BOT) ";
		} else {
			return "(" + ping + " ms) ";
		}
	}
	
	public String getNameShort() { 
		return name;
	}

	/**
	 * This abridges a name according to a max length in characters
	 * @param maxNameLen: the max characters
	 * @return the new abridged name
	 */
	public String getNameAbridged(int maxNameLen) {
		String displayedName = getNameShort();

		if (displayedName.length() > maxNameLen) {
			displayedName = displayedName.substring(0, maxNameLen).concat("...");
		}
		
		return displayedName;
	}

	public String getName() { return name; }

	public int getConnID() { return connID; }

	public boolean isWonLast() { return wonLast; }

	public int getWins() { return wins; }

	public void setWins(int wins) { this.wins = wins; }

	public int getKills() { return kills; }

	public void setKills(int kills) { this.kills = kills; }

	public int getDeaths() { return deaths; }

	public void setDeaths(int deaths) { this.deaths = deaths; }

	public int getAssists() { return assists; }

	public void setAssists(int assists) { this.assists = assists; }

	public int getScore() {	return score; }

	public void setScore(int score) { this.score = score; }

	public int getTeamScore() {	return teamScore; }

	public void setTeamScore(int teamScore) { this.teamScore = teamScore; }

	public int getExtraModeScore() { return extraModeScore; }

	public void setExtraModeScore(int extraModeScore) {	this.extraModeScore = extraModeScore; }

	public int getLives() { return lives; }

	public void setLives(int lives) { this.lives = lives; }

	public int getPing() { return ping; }

	public void setPing(int ping) { this.ping = ping; }
}
