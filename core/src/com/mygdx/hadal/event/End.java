package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.users.Transition.DEFAULT_FADE_DELAY;

/**
 * An End event makes the game end with either a victory or loss.
 * <p>
 * Triggered Behavior: When triggered, this will initiate the end of the game.
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * text: text that will appear in the results screen
 * 
 * @author Floley Flepperoncino
 */
public class End extends Event {

	private final String text;
	private final boolean victory;

	public End(PlayState state, String text, boolean victory) {
		super(state);
		this.text = text;
		this.victory = victory;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.levelEnd(text, victory, DEFAULT_FADE_DELAY);
			}
		};
	}
}
