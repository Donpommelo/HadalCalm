package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A PlayerMover is an event that transports the player elsewhere when they it is activated.
 * The event they are transported to does not have to be a portal.
 * 
 * Triggered Behavior: This triggers moving the player
 * Triggering Behavior: This event's connected event serves as the point that schmucks will be teleported to
 * 
 * Fields:
 * N/A
 * 
 
 * @author Zachary Tu
 *
 */
public class PlayerMover extends Event {

	private boolean moving = false;

	public PlayerMover(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					moving = true;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (moving) {
			if (getConnectedEvent().getBody() != null) {
				moving = false;
				state.getPlayer().setTransform(getConnectedEvent().getPosition(), 0);
				
				if (getConnectedEvent().getStandardParticle() != null) {
					getConnectedEvent().getStandardParticle().onForBurst(1.0f);
				}
			}
		}
	}
}
