package com.mygdx.hadal.save;

/**
 * Shared Settings containing all of the host's settings that should be visible/synced with the clients
 * @author Lussain Luggfish
 */
public class SharedSetting {

	//these are all the server settings that are visible to clients
	private int maxPlayers, artifactSlots, pvpTimer, lives;
	private boolean multiplayerPause;
	
	public SharedSetting() {}
			
	public SharedSetting(int maxPlayers, int artifactSlots, int pvpTimer, int lives, boolean multiplayerPause) {
		this.maxPlayers = maxPlayers;
		this.artifactSlots = artifactSlots;
		this.pvpTimer = pvpTimer;
		this.lives = lives;
		this.multiplayerPause = multiplayerPause;
	}

	public int getMaxPlayers() { return maxPlayers; }

	public int getArtifactSlots() {	return artifactSlots; }

	public int getPVPTimer() {	return pvpTimer; }

	public int getLives() {	return lives; }

	public boolean isMultiplayerPause() { return multiplayerPause; }
}
