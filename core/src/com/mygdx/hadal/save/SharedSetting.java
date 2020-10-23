package com.mygdx.hadal.save;

/**
 * Shared Settings containing all of the host's settings that should be visible/synced with the clients
 * @author Lussain Luggfish
 */
public class SharedSetting {

	//these are all the server settings that are visible to clients
	private int maxPlayers, pvpMode, artifactSlots, pvpTimer, coopTimer, lives, loadoutType;
	private boolean teamEnabled, multiplayerPause;
	
	public SharedSetting() {}
			
	public SharedSetting(int maxPlayers, int pvpMode, int artifactSlots, int pvpTimer, int coopTimer, int lives, int loadoutType,
						 boolean teamEnabled, boolean multiplayerPause) {
		this.maxPlayers = maxPlayers;
		this.pvpMode = pvpMode;
		this.artifactSlots = artifactSlots;
		this.pvpTimer = pvpTimer;
		this.coopTimer = coopTimer;
		this.lives = lives;
		this.loadoutType = loadoutType;
		this.teamEnabled = teamEnabled;
		this.multiplayerPause = multiplayerPause;
	}

	public int getMaxPlayers() { return maxPlayers; }

	public void setMaxPlayers(int maxPlayers) {	this.maxPlayers = maxPlayers; }

	public int getPvpMode() { return pvpMode; }

	public void setPvpMode(int pvpMode) { this.pvpMode = pvpMode; }

	public int getArtifactSlots() {	return artifactSlots; }

	public void setArtifactSlots(int artifactSlots) { this.artifactSlots = artifactSlots; }

	public int getPVPTimer() {	return pvpTimer; }

	public int getCoopTimer() {	return coopTimer; }

	public void setCoopTimer(int coopTimer) { this.coopTimer = coopTimer; }

	public int getLives() {	return lives; }

	public void setLives(int lives) { this.lives = lives; }

	public int getLoadoutType() { return loadoutType; }

	public void setLoadoutType(int loadoutType) { this.loadoutType = loadoutType; }

	public boolean isTeamEnabled() { return teamEnabled; }

	public void setTeamEnabled(boolean teamEnabled) { this.teamEnabled = teamEnabled; }

	public boolean isMultiplayerPause() { return multiplayerPause; }

	public void setMultiplayerPause(boolean multiplayerPause) { this.multiplayerPause = multiplayerPause; }
}
