package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A Stickyhitbox will stick to the first surface it hits, wall or schmuck.
 * This is currently used for: Stickybombs
 * @author Zachary Tu
 *
 */
public class StickyHitbox extends HitboxImage {

	private boolean stuckWall = false;
	private boolean stuckEnemy = false;
	private HadalEntity target;
	private Vector2 location;
	
	public StickyHitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura,
			float rest, Vector2 startVelo, short filter, boolean sensor, World world, OrthographicCamera camera,
			RayHandler rays, Schmuck creator, String spriteId) {
		super(state, x, y, width, height, grav, lifespan, dura, rest, startVelo, filter, sensor, world, camera, rays, creator,
				spriteId);
		
		this.setUserData(new HitboxData(state, world, this) {
			
			@Override
			public void onHit(final HadalData fixB) {
				
				//If not stuck yet and hitting a body, stick to it. A schmuck is saved.
				if (!stuckWall && !stuckEnemy) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
							stuckEnemy = true;
							target = fixB.getEntity();
							location = new Vector2(
									hbox.getBody().getPosition().x - target.getPosition().x, 
									hbox.getBody().getPosition().y - target.getPosition().y);		
						}
					} else {
						stuckWall = true;
						location = body.getPosition();
					}
				}
			}
		});	
	}
	
	@Override
	public void controller(float delta) {
		
		//If stuck to either a wall or schmuck, track its location and maintain same relative position.
		if (stuckWall && location != null) {
			body.setTransform(location, 0);
		}
		if (stuckEnemy && target != null && location != null) {
			if (target.isAlive()) {
				body.setTransform(target.getPosition().add(location), 0);
			} else {
				stuckEnemy = false;
			}
		}
		super.controller(delta);
	}

}
