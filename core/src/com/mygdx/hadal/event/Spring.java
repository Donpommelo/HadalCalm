package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Spring is an event that, when touched, will push an entity in a set direction
 * @author Zachary Tu
 *
 */
public class Spring extends Event {
	
	//The vector of force that will be applied to any touching entity.
	private Vector2 vec;

	private static final String name = "Spring";

	public Spring(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y, Vector2 vec) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.vec = vec;
	}
	
	public Spring(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y, Vector2 vec, float duration) {
		super(state, world, camera, rays, name, width, height, x, y, duration);
		this.vec = vec;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					fixB.getEntity().getBody().applyLinearImpulse(vec, fixB.getEntity().getBody().getWorldCenter(), true);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}	
}
