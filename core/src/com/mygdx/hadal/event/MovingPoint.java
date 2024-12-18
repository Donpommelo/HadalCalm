package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

import static com.mygdx.hadal.constants.Constants.PPM;
import static com.mygdx.hadal.states.PlayState.SYNC_TIME;

/**
 * This is a platform that continuously moves towards its connected event.
 * <p>
 * Triggered Behavior: When triggered, the platform's connected event will be set to the event that triggered it.
 * Triggering Behavior: This event will move continually to the position of its connected event. When it touches its connected event,
 * 	the platform will set its connected event to its connected event's connected event if possible .
 * <p>
 * Fields:
 * speed: float determining the platform's speed. Optional. Default: 1.0f
 * pause: boolean of whether the platform will stop moving if it activates an event. Default false.
 * syncConnected: boolean of whether events connected to this should be synced with the client or not. Default true
 * <p>
 * connections: This is a comma-separated list of triggeredIds of events that are "connected" to this platform and will move along it.
 * 	NOTES: DO NOT CONNECT ANY EVENTS THAT DO NOT HAVE A BODY; TIMERS, COUNTERS, ETC.
 * 
 * @author Nijfruit Nerzupoulos
 *
 */
public class MovingPoint extends Event {

	private final float speed;
	private final boolean pause, syncConnected;
	
	private final ObjectMap<Event, Vector2> connected = new ObjectMap<>();
	
	public MovingPoint(PlayState state, Vector2 startPos, Vector2 size, float speed, boolean pause, boolean syncConnected) {
		super(state, startPos, size);
		this.speed = speed;
		this.pause = pause;
		this.syncConnected = syncConnected;
	}

	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataType.WALL) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				event.setConnectedEvent(activator.getEvent());
				needsToStartMoving = true;
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.setSensor(false)
				.addToWorld(world);
	}


	//this tracks if the event has started moving yet. Used so we can set event velocity instead of transforming every frame.
	private boolean needsToStartMoving = true;
	private final Vector2 dist = new Vector2();
	private final Vector2 targetPosition = new Vector2();
	private final Vector2 tempConnectedPosition = new Vector2();

	//we keep track of distance from target point. If our distance increases, we overshot the target.
	private float lastDistLength;
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			if (getConnectedEvent().getBody() != null) {
				targetPosition.set(getConnectedEvent().getPosition());
				dist.set(targetPosition).sub(getPosition());

				float distLength = dist.len2();

				//if distance is too great, set to move this and connected events towards connected event.
				if (needsToStartMoving && dist.len2() > delta * delta * speed * speed) {
					needsToStartMoving = false;
					setLinearVelocity(dist.nor().scl(speed));

					//Move all connected events by same amount.
					for (Event e : connected.keys()) {
						e.setLinearVelocity(dist.nor().scl(speed));
					}
				} else if (!needsToStartMoving && (dist.len2() < delta * delta * speed * speed ||
						(lastDistLength != 0 && lastDistLength < distLength))) {

					//reset saved distance when changing target point
					distLength = 0.0f;

					//when we approach our target, tell the event that we need to set velocity again
					needsToStartMoving = true;
					setTransform(targetPosition, 0);
					for (ObjectMap.Entry<Event, Vector2> e : connected.entries()) {
						tempConnectedPosition.set(targetPosition).add(e.value);
						e.key.setTransform(tempConnectedPosition, 0);
					}

					//if there isn't another rally point, stop moving
					if (getConnectedEvent().getConnectedEvent() == null) {
						setLinearVelocity(0, 0);

						//Move all connected events by same amount.
						for (Event e : connected.keys()) {
							e.setLinearVelocity(0, 0);
						}
					} else {

						//activate next event in the chain and move to next rally point
						getConnectedEvent().getConnectedEvent().getEventData().preActivate(eventData, null);
						if (getConnectedEvent().getConnectedEvent().getBody() != null) {
							setConnectedEvent(getConnectedEvent().getConnectedEvent());
						} else {
							if (pause) {
								setLinearVelocity(0, 0);
								for (Event e : connected.keys()) {
									e.setLinearVelocity(0, 0);
								}
							}
							setConnectedEvent(null);
						}
					}
				}

				//this line updates relative location of connected events in case they are dynamic (timed pickups)
				for (ObjectMap.Entry<Event, Vector2> e : connected.entries()) {
					connected.put(e.key, new Vector2(e.key.getPosition()).sub(getPosition()));
				}

				lastDistLength = distLength;
			}
		} 
	}

	private boolean startSynced;
	private float syncDelayCount;
	@Override
	public void clientController(float delta) {
		controller(delta);
		if (!startSynced) {
			syncDelayCount += delta;

			if (syncDelayCount >= 2.0f) {
				startSynced = true;
				PacketManager.clientTCP(new Packets.RequestStartSyncedEvent(triggeredID));
			}
		}
	}

	/**
	 * Add another event to be connected to this moving point. Connected events must be synced
	 */
	public void addConnection(Event e) {
		if (e != null) {
			connected.put(e, new Vector2(e.getStartPos()).sub(startPos).scl(1 / PPM));
			if (syncConnected) {
				e.setIndependent(false);
				e.setSynced(true);
			} else {
				e.setIndependent(true);
			}
		}
	}

	private final Vector2 delayedPosition = new Vector2();
	@Override
	public Object onServerSyncInitial() {
		if (getConnectedEvent() == null) {
			return new Packets.CreateStartSyncedEvent(state.getTimer(), triggeredID, null, getPosition(), getLinearVelocity());
		} else {
			return new Packets.CreateStartSyncedEvent(state.getTimer(), triggeredID, getConnectedEvent().getTriggeredID(), getPosition(), getLinearVelocity());
		}
	}

	@Override
	public void onClientSyncInitial(float timer, Event target, Vector2 position, Vector2 velocity) {
		delayedPosition.set(position).add(velocity.scl(-2 * SYNC_TIME));

		//in case of mid-game join, client needs server's current location, not starting location
		setTransform(delayedPosition, 0);
		for (ObjectMap.Entry<Event, Vector2> e : connected.entries()) {
			tempConnectedPosition.set(delayedPosition).add(e.value);
			e.key.setTransform(tempConnectedPosition, 0);
		}
		setConnectedEvent(target);
		needsToStartMoving = true;
	}

	/**
	 * This returns all connected events. This is so that event movers can move all connected events at once.
	 */
	public ObjectMap<Event, Vector2> getConnected() { return connected; }
}
