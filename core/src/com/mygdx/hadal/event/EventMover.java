package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An EventMover. This Event will move a specified event to its own location.
 * 
 * Note that moving objects during physics step is not good. Because an event can be activated any time, this event,
 * when triggered, will wait until the next engine tick to actually perform the move safely.
 * 
 * Also, as an extra note, deleting + cloing do not have this problem b/c adding + removing stuff is already done safely
 * 
 * Triggered Behavior: When triggered, this eventwill perform the move.
 * Triggering Behavior: The connected event is the one who will be moved.
 * 
 * Note that many events do not have bodies. Attempting to move them will do nothing.
 * 
 * Fields:
 * 
 * gravity: Specifies whether to make the newly-moved object have gravity. Optional. Default: 0.0f
 * (This pretty much only exists to make the NASU minigame work)
 * 
 * @author Zachary Tu
 *
 */
public class EventMover extends Event {
	
	private static final String name = "Event Mover";

	private float gravity;
	private boolean moving = false;
	
	public EventMover(PlayState state, int width, int height, int x, int y, float gravity) {
		super(state, name, width, height, x, y);
		this.gravity = gravity;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					if (event.getConnectedEvent().getBody() != null) {
						moving = true;
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (moving) {
			moving = false;
			if (gravity != -1) {
				getConnectedEvent().getBody().setGravityScale(gravity);
			}
			getConnectedEvent().getBody().setTransform(getBody().getPosition(), 0);
			
			if (standardParticle != null) {
				standardParticle.onForBurst(1.0f);
			}
		}
	}
	
}
