package com.mygdx.hadal.save;

public class SharedSetting {

	private int maxPlayers, pvpMode, artifactSlots, timer, lives, loadoutType;
	private boolean multiplayerPause;
	
	public SharedSetting() {}
			
	public SharedSetting(int maxPlayers, int pvpMode, int artifactSlots, int timer, int lives, int loadoutType, boolean multiplayerPause) {
		this.maxPlayers = maxPlayers;
		this.pvpMode = pvpMode;
		this.artifactSlots = artifactSlots;
		this.timer = timer;
		this.lives = lives;
		this.loadoutType = loadoutType;
		this.multiplayerPause = multiplayerPause;
	}

	public int getMaxPlayers() { return maxPlayers; }

	public void setMaxPlayers(int maxPlayers) {	this.maxPlayers = maxPlayers; }

	public int getPvpMode() { return pvpMode; }

	public void setPvpMode(int pvpMode) { this.pvpMode = pvpMode; }

	public int getArtifactSlots() {	return artifactSlots; }

	public void setArtifactSlots(int artifactSlots) { this.artifactSlots = artifactSlots; }

	public int getTimer() {	return timer; }

	public void setTimer(int timer) { this.timer = timer; }

	public int getLives() {	return lives; }

	public void setLives(int lives) { this.lives = lives; }

	public int getLoadoutType() { return loadoutType; }

	public void setLoadoutType(int loadoutType) { this.loadoutType = loadoutType; }

	public boolean isMultiplayerPause() { return multiplayerPause; }

	public void setMultiplayerPause(boolean multiplayerPause) { this.multiplayerPause = multiplayerPause; }
}
