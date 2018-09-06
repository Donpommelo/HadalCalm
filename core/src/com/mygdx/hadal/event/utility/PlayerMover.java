package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Use Portal is a portal that transports the player elsewhere when they interact with it.
 * The event they are transported to does not have to be a portal.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: This event's connected event serves as the point that schmucks will be teleported to
 * 
 * Fields:
 * N/A
 * 
 
 * @author Zachary Tu
 *
 */
public class PlayerMover extends Event {

	private static final String name = "Player Mover";
	private boolean moving = false;

	public PlayerMover(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					moving = true;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (moving) {
			moving = false;
			state.getPlayer().getBody().setTransform(getConnectedEvent().getBody().getPosition(), 0);
			
			if (getConnectedEvent().getStandardParticle() != null) {
				getConnectedEvent().getStandardParticle().onForBurst(1.0f);
			}
		}
	}
}
