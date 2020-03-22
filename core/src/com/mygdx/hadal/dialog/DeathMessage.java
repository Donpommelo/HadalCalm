package com.mygdx.hadal.dialog;

/**
 * A Death message contains the info for a single message notification displayed when a player dies.
 * This information is read from jason file text/DeathMessages.json
 * @author Zachary Tu
 *
 */
public class DeathMessage {

	//This is the message that will be displayed
	private String message;
	
	//This represents the relative frequency of the message
	private int weight;
	
	//if the player is killed by another player, the message contains names of both perp and vic.
	private boolean namedPerp;
	
	public DeathMessage() {}

	public String getMessage() { return message; }

	public int getWeight() { return weight; }
	
	public boolean isNamedPerp() { return namedPerp; }
}
