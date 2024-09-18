package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A Level Warp transports the player to another level.
 * <p>
 * Triggered Behavior: warps the player to desired level
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * level: The string filename of the level that the player will be warped to.
 * reset: should we reset the player's loadout? Optional. Default: false.
 * startId: which start event should we start at? 
 * 
 * @author Blascoe Bustule
 */
public class LevelWarp extends Event {

	private final String level;
	private final boolean reset;
	private final String startId;
	
	//have we warped yet?
	private boolean warpStart;

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
				state.getTransitionManager().loadLevel(UnlockLevel.getByName(level), TransitionState.NEWLEVEL, startId);
			} else {
				state.getTransitionManager().loadLevel(UnlockLevel.getByName(level), TransitionState.NEXTSTAGE, startId);
			}
		}
	}
}
