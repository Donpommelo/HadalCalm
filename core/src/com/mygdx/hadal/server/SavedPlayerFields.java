package com.mygdx.hadal.server;

public class SavedPlayerFields {
	private String name;
	private int wins, kills, deaths, killStreak, deathStreak, score;
	
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
	}
	
	public String getKD() {
		return kills + "/" + deaths;
	}
	
	public void registerKill() {
		kills++;
		killStreak++;
		score++;
		
		deathStreak = 0;
	}
	
	public void registerDeath() {
		deaths++;
		deathStreak++;
		
		if (score > 0) {
			score--;
		}
		
		killStreak = 0;
	}

	public void newLevelReset() {
		this.kills = 0;
		this.deaths = 0;
		this.killStreak = 0;
		this.deathStreak = 0;
		this.score = 0;
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

	public void setWins(int wins) {
		this.wins = wins;
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
}


