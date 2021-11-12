package com.mygdx.hadal.server.packets;

/**
 * A PacketEffect is simply a way to run a block of code as a result of a packet.
 * These PacketEffects are stored in the PlayState and processed during the update()
 * Running stuff here prevents weird concurrency issues. 
 * (Probably at the cost of a bit of performance b/c everything is being run in the same thread)
 * @author Shudimir Stimingo
 */
public interface PacketEffect {
	
	/**
	 * Do the thing
	 */
	void execute();
}
