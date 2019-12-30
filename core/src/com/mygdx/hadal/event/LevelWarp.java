package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.transitionState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Use Portal is a portal that transports the player elsewhere when they interact with it.
 * The event they are transported to does not have to be a portal.
 * 
 * Triggered Behavior: N/A
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
	
	public LevelWarp(PlayState state, Vector2 startPos, Vector2 size, String level, boolean reset, String startId) {
		super(state, startPos, size);
		this.level = level;
		this.startId = startId;
		this.reset = reset;
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				if (reset) {
					state.loadLevel(UnlockLevel.valueOf(level), transitionState.NEWLEVEL, false, startId);
				} else {
					state.loadLevel(UnlockLevel.valueOf(level), transitionState.NEXTSTAGE, true, startId);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.PYRAMID);
	}
}
