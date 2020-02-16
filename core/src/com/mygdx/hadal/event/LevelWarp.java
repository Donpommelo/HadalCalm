package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

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
	private boolean warpStart, reset;
	
	public LevelWarp(PlayState state, String level, boolean reset, String startId) {
		super(state);
		this.level = level;
		this.reset = reset;
		this.startId = startId;
		
		warpStart = false;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				warpStart = true;
			}
		};
	}
	
	@Override
	public void controller(float delta) {
		if (warpStart && state.isServer()) {
			warpStart = false;
			
			if (reset) {
				state.loadLevel(UnlockLevel.valueOf(level), TransitionState.NEWLEVEL, startId);
			} else {
				state.loadLevel(UnlockLevel.valueOf(level), TransitionState.NEXTSTAGE, startId);
			}
		}
	}
}
