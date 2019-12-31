package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.transitionState;

/**
 * A Use Portal is a portal that transports the player elsewhere when they interact with it.
 * The event they are transported to does not have to be a portal.
 * 
 * Triggered Behavior: warps the player to desired level
 * Triggering Behavior: N/A
 * 
 * Fields:
 * level: The string filename of the level that the player will be warped to.
 * reset: boolean determining whether the player's loadout/hp/statuses will be reset. Optiona;. Default: true.
 * 
 * @author Zachary Tu
 *
 */
public class LevelWarp extends Event {

	private String level;
	private String startId;
	private boolean reset;
	
	public LevelWarp(PlayState state, String level, boolean reset, String startId) {
		super(state);
		this.level = level;
		this.startId = startId;
		this.reset = reset;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (reset) {
					state.loadLevel(UnlockLevel.valueOf(level), transitionState.NEWLEVEL, false, startId);
				} else {
					state.loadLevel(UnlockLevel.valueOf(level), transitionState.NEXTSTAGE, true, startId);
				}
			}
		};
	}
}
