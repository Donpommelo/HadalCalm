package com.mygdx.hadal.save;

/**
 * Shared Settings containing all of the host's settings that should be visible/synced with the clients
 * @author Lussain Luggfish
 */
public class SharedSetting {

	//these are all the server settings that are visible to clients
	private int maxPlayers, pvpMode, artifactSlots, pvpTimer, pvpHp, coopTimer, lives, loadoutType, teamType;
	private boolean multiplayerPause;
	
	public SharedSetting() {}
			
	public SharedSetting(int maxPlayers, int pvpMode, int artifactSlots, int pvpTimer, int pvpHp, int coopTimer,
						 int lives, int loadoutType, int teamType, boolean multiplayerPause) {
		this.maxPlayers = maxPlayers;
		this.pvpMode = pvpMode;
		this.artifactSlots = artifactSlots;
		this.pvpTimer = pvpTimer;
		this.pvpHp = pvpHp;
		this.coopTimer = coopTimer;
		this.lives = lives;
		this.loadoutType = loadoutType;
		this.teamType = teamType;
		this.multiplayerPause = multiplayerPause;
	}

	public int getMaxPlayers() { return maxPlayers; }

	public int getPvpMode() { return pvpMode; }

	public int getArtifactSlots() {	return artifactSlots; }

	public int getPVPTimer() {	return pvpTimer; }

	public int getPVPHp() {	return pvpHp; }

	public int getCoopTimer() {	return coopTimer; }

	public int getLives() {	return lives; }

	public int getLoadoutType() { return loadoutType; }

	public int getTeamType() { return teamType; }

	public boolean isMultiplayerPause() { return multiplayerPause; }
}
