package com.mygdx.hadal.event.ai;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * This is an experimental ai strategy that is dropped continuously by the player and is attractive to enemies.
 * @author Zachary Tu
 *
 */
public class PlayerTrail extends Event {

	private final static String name = "Trail";
	
	private final static int width = 64;
	private final static int height = 64;
	
	private static final float lifespan = 20.0f;
	private float lifeLeft;
	
	private PlayerTrail nextTrail;
	
	public PlayerTrail(PlayState state, World world, OrthographicCamera camera, RayHandler ray, int x, int y) {
		super(state, world, camera, ray, name, width, height, x, y);
		this.lifeLeft = lifespan;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_ENEMY | Constants.BIT_SENSOR),
				(short) 0, true, eventData);
		
	}
	
	@Override
	public void controller(float delta) {
		lifeLeft -= delta;
		if (lifeLeft <= 0) {
			queueDeletion();
		}
		
		for (HadalEntity schmuck : eventData.getSchmucks()) {
			if (schmuck instanceof Enemy && nextTrail != null) {
				if (((Enemy)schmuck).getTarget() instanceof PlayerTrail) {
					if (((PlayerTrail)((Enemy)schmuck).getTarget()).lifeLeft <= lifeLeft) {
						((Enemy)schmuck).setTarget(nextTrail);
					}
				} else {
					((Enemy)schmuck).setTarget(nextTrail);
				}
			}
		}
	}
	
	public void setTrail(PlayerTrail newTrail) {
		nextTrail = newTrail;
	}
	
	/*public String getText() {
		if (nextTrail != null) {
			if (nextTrail.getBody() != null) {
				return body.getPosition() + "Trail to " + nextTrail.body.getPosition();
			}
		}
		return body.getPosition() + "Trail to " + nextTrail;

	}*/
	

}
