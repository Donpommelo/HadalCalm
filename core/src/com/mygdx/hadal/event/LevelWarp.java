package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.GameState.TransitionState;

/**
 * A Level Warp transports the player to another level.
 * 
 * Triggered Behavior: warps the player to desired level
 * Triggering Behavior: N/A
 * 
 * Fields:
 * level: The string filename of the level that the player will be warped to.
 * reset: should we reset the player's loadout? Optional. Default: false.
 * instant: should we transport instantly or have a transition?. Optional. Default: true.
 * 
 * @author Zachary Tu
 *
 */
public class LevelWarp extends Event {

	private String level;
	private String startId;
	private boolean instant, reset;
	
	public LevelWarp(PlayState state, String level, boolean reset, boolean instant, String startId) {
		super(state);
		this.level = level;
		this.reset = reset;
		this.instant = instant;
		this.startId = startId;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (reset) {
					state.loadLevel(UnlockLevel.valueOf(level), TransitionState.NEWLEVEL, instant, startId);
				} else {
					state.loadLevel(UnlockLevel.valueOf(level), TransitionState.NEXTSTAGE, instant, startId);
				}
			}
		};
	}
}
