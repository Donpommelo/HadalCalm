package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class StickyHitbox extends HitboxImage {

	public boolean stuckWall = false;
	public boolean stuckEnemy = false;
	public Schmuck target;
	public Vector2 location;
	
	public StickyHitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura,
			float rest, Vector2 startVelo, short filter, boolean sensor, World world, OrthographicCamera camera,
			RayHandler rays, Schmuck creator, String spriteId) {
		super(state, x, y, width, height, grav, lifespan, dura, rest, startVelo, filter, sensor, world, camera, rays, creator,
				spriteId);
		
		this.setUserData(new HitboxData(state, world, this) {
			
			public void onHit(final HadalData fixB) {
				if (!stuckWall && !stuckEnemy) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							stuckEnemy = true;
							target = ((BodyData)fixB).getSchmuck();
							location = new Vector2(
									hbox.getBody().getPosition().x - target.getPosition().x, 
									hbox.getBody().getPosition().y - target.getPosition().y);		
						}
						if (fixB.getType().equals(UserDataTypes.WALL)) {
							stuckWall = true;
							location = body.getPosition();				
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
		if (stuckWall && location != null) {
			body.setTransform(location, 0);
		}
		if (stuckEnemy && target != null && location != null) {
			if (target.alive) {
				body.setTransform(target.getPosition().add(location), 0);
			} else {
				stuckEnemy = false;
			}
		}
		super.controller(delta);
	}

}
