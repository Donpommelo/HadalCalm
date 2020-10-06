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
 * startId: which start event should we start at? 
 * 
 * @author Zachary Tu
 */
public class LevelWarp extends Event {

	private final String level;
	private final String startId;
	
	//have we warped yet? Do we reset player stats on warp?
	private boolean warpStart;
	private final boolean reset;
	
	public LevelWarp(PlayState state, String level, boolean reset, String startId) {
		super(state);
		this.level = level;
		this.reset = reset;
		this.startId = startId;
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
		
		//check warpStart to avoid double loading level. Only the server does warps.
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
