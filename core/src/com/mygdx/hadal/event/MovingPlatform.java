package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * This is a platform that continuously moves towards its connected event.
 * 
 * Triggered Behavior: When triggered, the platform's connected event will be set to the event that triggered it.
 * Triggering Behavior: This event will move continually to the position of its connected event. When it touches its connected event,
 * 	the platform will set its connected event to its connected event's connected event if possible .
 * 
 * Fields:
 * speed: float determining the platform's speed. Optional. Default: 1.0f
 * 
 * connections: This is a comma-separated list of triggeredIds of events that are "connected" to this platform and will move along it.
 * 	NOTES: DO NOT CONNECT ANY EVENTS THAT DO NOT HAVE A BODY; TIMERS, COUNTERS, ETC.
 * 
 * @author Zachary Tu
 *
 */
public class MovingPlatform extends Event {

	private static final String name = "Moving Platform";

	private float speed;
	
	private ArrayList<Event> connected = new ArrayList<Event>();
	
	public MovingPlatform(PlayState state, World world, OrthographicCamera camera, RayHandler rays,
			int width, int height, int x, int y, float speed) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.speed = speed;
	}

	@Override
	public void create() {

		this.eventData = new EventData(world, this, UserDataTypes.WALL) {
			
			@Override
			public void onActivate(EventData activator) {
				event.setConnectedEvent(activator.getEvent());
			}

		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, 5.0f, false, true, Constants.BIT_WALL, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR),
				(short) 0, false, eventData);
		
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
	
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			Vector2 dist = getConnectedEvent().getBody().getPosition().sub(body.getPosition()).scl(PPM);

			//If this platform is close enough to its connected event, move to the next event in the chain.
			if ((int)dist.len2() <= 1) {
				
				//If no more connected events, make the platfor mand all connected events stop moving.
				if (getConnectedEvent().getConnectedEvent() == null) {
					body.setLinearVelocity(0, 0);
					
					//Move all connected events by same amount.
					for (Event e : connected) {
						if (e.getBody() != null && e.isAlive()) {
							e.getBody().setLinearVelocity(0, 0);
						}
					}
				} else {
					body.setTransform(getConnectedEvent().getBody().getPosition(), 0);
					setConnectedEvent(getConnectedEvent().getConnectedEvent());
				}
			} else {
				
				//Continually move towards connected event.				
				body.setLinearVelocity(dist.nor().scl(speed));
				
				//Move all connected events by same amount.
				for (Event e : connected) {
					if (e.getBody() != null && e.isAlive()) {
						e.getBody().setLinearVelocity(dist.nor().scl(speed));
					}
				}
			}
		} else {
			
			//If no connected events, all connected events should stand still.
			for (Event e : connected) {
				if (e.getBody() != null && e.isAlive()) {
					e.getBody().setLinearVelocity(0, 0);
				}
			}

		}
	}
	
	public void addConnection(Event e) {
		if (e != null) {
			connected.add(e);
		}
	}

}
