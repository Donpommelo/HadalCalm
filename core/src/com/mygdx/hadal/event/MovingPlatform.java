package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;
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
 * @author Zachary Tu
 *
 */
public class MovingPlatform extends Event {

	private static final String name = "Moving Platform";

	private float speed;
	
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
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR | Constants.BIT_WALL),
				(short) 0, false, eventData);
		
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
	
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			Vector2 dist = getConnectedEvent().getBody().getPosition().sub(body.getPosition()).scl(PPM);

			if ((int)dist.len2() <= 0 && getConnectedEvent().getConnectedEvent() != null) {
				setConnectedEvent(getConnectedEvent().getConnectedEvent());
			} else {
				body.setLinearVelocity(dist.nor().scl(speed));
			}
		}
	}

}
