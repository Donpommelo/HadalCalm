package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.MovingPlatform;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An EventMover. This Event will move a specified event to its own location.
 * 
 * Note that moving objects during physics step is not good. Because an event can be activated any time, this event,
 * when triggered, will wait until the next engine tick to actually perform the move safely.
 * 
 * Also, as an extra note, deleting + cloning do not have this problem b/c adding + removing stuff is already done safely
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
			public void onActivate(EventData activator, Player p) {
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
			
			Vector2 dist = new Vector2(getPosition().sub(getConnectedEvent().getPosition()));
			getConnectedEvent().getBody().setTransform(getPosition(), 0);
			
			if (getConnectedEvent() instanceof MovingPlatform) {
				for (Event connect : ((MovingPlatform)getConnectedEvent()).getConnected()) {
					connect.getBody().setTransform(connect.getPosition().add(dist), 0);
				}
			}
			
			if (standardParticle != null) {
				standardParticle.onForBurst(1.0f);
			}
		}
	}
}
