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
 * text: text that will appear in the results screen
 * 
 * @author Zachary Tu
 */
public class End extends Event {

	//This is the text displayed in the results screen.
	private final String text;
	
	public End(PlayState state, String text) {
		super(state);
		this.text = text;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.levelEnd(text);
			}
		};
	}
}
