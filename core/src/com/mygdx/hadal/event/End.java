package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * An End event makes the game end with either a victory or loss.
 * 
 * Triggered Behavior: When triggered, this will initiate the end of the game.
 * Triggering Behavior: N/A
 * 
 * 
 * Fields:
 * won: boolean that determines if the player wins or not. Optional. Default: true
 * 
 * @author Zachary Tu
 *
 */
public class End extends Event {

	private static final String name = "VICTORY";

	private boolean won;
	
	public End(PlayState state, boolean won) {
		super(state, name);
		this.won = won;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.gameOver(won);
			}
		};
	}
}
