package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.MovingPoint;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An EventMover. This Event will move a specified event to its own location.
 * 
 * Note that moving objects during physics step is not good. Because an event can be activated any time, this event,
 * when triggered, will wait until the next engine tick to actually perform the move safely.
 * 
 * Also, as an extra note, deleting + cloning do not have this problem b/c adding + removing stuff is already done safely
 * 
 * Triggered Behavior: When triggered, this event will perform the move.
 * Triggering Behavior: The connected event is the one who will be moved.
 * 
 * Note that many events do not have bodies. Attempting to move them will do nothing.
 * 
 * Fields:
 * 
 * gravity: Specifies whether to make the newly-moved object have gravity. Optional. Default: 0.0f
 * (This pretty much only exists to make the NASU minigame work)
 * 
 * @author Shirpurlsberg Slexanne
 */
public class EventMover extends Event {
	
	private final float gravity;
	
	//are we in the middle of moving the event?
	private boolean moving;
	
	public EventMover(PlayState state, Vector2 startPos, Vector2 size, float gravity) {
		super(state, startPos, size);
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
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
		this.body.setType(BodyType.KinematicBody);
	}
	
	private final Vector2 dist = new Vector2();
	private final Vector2 entityPosition = new Vector2();
	@Override
	public void controller(float delta) {
		if (moving) {
			moving = false;
			if (gravity != -1) {
				getConnectedEvent().setGravityScale(gravity);
			}
			entityPosition.set(getPosition());
			dist.set(entityPosition).sub(getConnectedEvent().getPosition());
			getConnectedEvent().setTransform(entityPosition, 0);
			
			if (getConnectedEvent() instanceof MovingPoint point) {
				for (Event connect : point.getConnected().keys()) {
					connect.setTransform(connect.getPosition().add(dist), 0);
				}
			}
			
			if (standardParticle != null) {
				standardParticle.onForBurst(1.0f);
			}
		}
	}
}
