package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * An event deleter TBA
 * @author Zachary Tu
 *
 */
public class EventMover extends Event {
	
	private static final String name = "Event Mover";

	private float gravity;
	private boolean moving = false;
	
	private ParticleEntity particle;
	
	public EventMover(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float gravity) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.gravity = gravity;
		
		particle = new ParticleEntity(state, world, camera, rays, this, AssetList.EVENT_HOLO.toString(), 1.0f, false);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
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
			
			particle.onForBurst(1.0f);
		}
	}
	
}
