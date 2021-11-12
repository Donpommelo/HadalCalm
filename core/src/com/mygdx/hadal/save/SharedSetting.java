package com.mygdx.hadal.save;

/**
 * Shared Settings containing all of the host's settings that should be visible/synced with the clients
 * @author Lussain Luggfish
 */
public class SharedSetting {

	//these are all the server settings that are visible to clients
	private int maxPlayers, artifactSlots;
	private boolean multiplayerPause;
	
	public SharedSetting() {}
			
	public SharedSetting(int maxPlayers, int artifactSlots, boolean multiplayerPause) {
		this.maxPlayers = maxPlayers;
		this.artifactSlots = artifactSlots;
		this.multiplayerPause = multiplayerPause;
	}

	public int getMaxPlayers() { return maxPlayers; }

	public int getArtifactSlots() {	return artifactSlots; }

	public boolean isMultiplayerPause() { return multiplayerPause; }
}
