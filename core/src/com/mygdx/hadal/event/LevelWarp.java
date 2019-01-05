package com.mygdx.hadal.event;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
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

	private static final String name = "Level Warp";

	private String level;
	private boolean reset;
	
	public LevelWarp(PlayState state, int width, int height, int x, int y, String level, boolean reset) {
		super(state, name, width, height, x, y);
		this.level = level;
		this.reset = reset;
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				state.loadLevel(UnlockLevel.valueOf(level), reset);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		return name + " (E TO USE)";
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.PYRAMID);
	}
}
