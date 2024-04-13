package com.mygdx.hadal.users;

/**
 * This represents saved fields for a player.
 * Atm, extraModeScore is used for revive timer (to keep track of how many times player was revived) and trick-or-treat candy
 * @author Medelaire Morcester
 */
public class ScoreManager {
	
	//Player's stored score fields for the score window
	private int wins, currency, kills, deaths, assists, score, teamScore, extraModeScore;
	private int lives = 1;

	//did the player win their last game? This is used to let the client know who won in the results screen.
	private boolean wonLast, ready;
	
	//this unused constructor is needed by kryo for serialization
	public ScoreManager() {}
	
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

	public boolean isWonLast() { return wonLast; }

	public int getWins() { return wins; }

	public void setWins(int wins) { this.wins = wins; }

	public int getCurrency() { return currency; }

	public void setCurrency(int currency) { this.currency = currency; }

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

	public boolean isReady() { return ready; }

	public void setReady(boolean ready) { this.ready = ready; }
}
