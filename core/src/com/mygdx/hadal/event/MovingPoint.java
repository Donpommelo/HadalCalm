package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This is a platform that continuously moves towards its connected event.
 * 
 * Triggered Behavior: When triggered, the platform's connected event will be set to the event that triggered it.
 * Triggering Behavior: This event will move continually to the position of its connected event. When it touches its connected event,
 * 	the platform will set its connected event to its connected event's connected event if possible .
 * 
 * Fields:
 * speed: float determining the platform's speed. Optional. Default: 1.0f
 * pause: boolean of whether the platform will stop moving if it activates an event. Default false.
 * syncConnected: boolean of whether events connected to this should be synced with the client or not. Default true
 * 
 * connections: This is a comma-separated list of triggeredIds of events that are "connected" to this platform and will move along it.
 * 	NOTES: DO NOT CONNECT ANY EVENTS THAT DO NOT HAVE A BODY; TIMERS, COUNTERS, ETC.
 * 
 * @author Nijfruit Nerzupoulos
 *
 */
public class MovingPoint extends Event {

	private final float speed;
	private final boolean pause, syncConnected;
	
	private final HashMap<Event, Vector2> connected = new HashMap<>();
	
	public MovingPoint(PlayState state, Vector2 startPos, Vector2 size, float speed, boolean pause, boolean syncConnected) {
		super(state, startPos, size);
		this.speed = speed;
		this.pause = pause;
		this.syncConnected = syncConnected;
	}

	@Override
	public void create() {

		this.eventData = new EventData(this, UserDataTypes.WALL) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				event.setConnectedEvent(activator.getEvent());
				needsToStartMoving = true;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, false, true, Constants.BIT_SENSOR, (short) 0, (short) 0, false, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}


	private boolean needsToStartMoving = true;
	private final Vector2 dist = new Vector2();
	private final Vector2 targetPosition = new Vector2();
	private final Vector2 tempConnectedPosition = new Vector2();
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			if (getConnectedEvent().getBody() != null) {
				targetPosition.set(getConnectedEvent().getPosition());
				dist.set(targetPosition).sub(getPosition());

				if (needsToStartMoving && dist.len2() > delta * delta * speed * speed) {
					needsToStartMoving = false;
					//Continually move towards connected event.
					setLinearVelocity(dist.nor().scl(speed));

					//Move all connected events by same amount.
					for (Event e : connected.keySet()) {
						e.setLinearVelocity(dist.nor().scl(speed));
					}
				} else if (!needsToStartMoving && dist.len2() < delta * delta * speed * speed) {
					needsToStartMoving = true;
					setTransform(targetPosition, 0);
					for (Map.Entry<Event, Vector2> e : connected.entrySet()) {
						tempConnectedPosition.set(targetPosition).add(e.getValue());
						e.getKey().setTransform(tempConnectedPosition, 0);
					}

					if (getConnectedEvent().getConnectedEvent() == null) {
						setLinearVelocity(0, 0);

						//Move all connected events by same amount.
						for (Event e : connected.keySet()) {
							e.setLinearVelocity(0, 0);
						}
					} else {
						getConnectedEvent().getConnectedEvent().getEventData().preActivate(eventData, null);
						if (getConnectedEvent().getConnectedEvent().getBody() != null) {
							setConnectedEvent(getConnectedEvent().getConnectedEvent());
						} else {
							if (pause) {
								setLinearVelocity(0, 0);
								for (Event e : connected.keySet()) {
									e.setLinearVelocity(0, 0);
								}
							}
							setConnectedEvent(null);
						}
					}
				}
			}
		} 
	}
	
	/**
	 * Add another event to be connected to this moving point
	 */
	public void addConnection(Event e) {
		if (e != null) {
			connected.put(e, new Vector2(e.getStartPos()).sub(startPos).scl(1 / PPM));
			if (syncConnected) {
				e.setSynced(true);
				e.setIndependent(false);
			}
		}
	}

	/**
	 * This returns all connected events. This is so that event movers can move all connected events at once.
	 */
	public HashMap<Event, Vector2> getConnected() { return connected; }
}
