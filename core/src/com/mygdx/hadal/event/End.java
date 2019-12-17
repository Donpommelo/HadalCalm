package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.transitionState;

/**
 * An End event makes the game end with either a victory or loss.
 * 
 * Triggered Behavior: When triggered, this will initiate the end of the game.
 * Triggering Behavior: N/A
 * 
 * 
 * Fields:
 * text: text that will appear in the results screen
 * 
 * @author Zachary Tu
 *
 */
public class End extends Event {

	private static final String name = "VICTORY";

//	private String text;
	
	public End(PlayState state, String text) {
		super(state, name);
//		this.text = text;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.levelEnd(transitionState.RESULTS);
			}
		};
	}
}
