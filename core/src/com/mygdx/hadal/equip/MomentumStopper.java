package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.event.MomentumPickup;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class MomentumStopper extends RangedWeapon {

	private final static String name = "Momentum Stopper";
	private final static int clipSize = 1;
	private final static int shootCd = 0;
	private final static int shootDelay = 0;
	private final static float reloadTime = 0.0f;
	private final static int reloadAmount = 1;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 0.01f;
	private final static int projectileWidth = 800;
	private final static int projectileHeight = 800;
	private final static int lifespan = 10;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			Hitbox proj = new Hitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, startVelocity,
					filter, true, world, camera, rays);

			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			proj.setUserData(new HitboxData(state, world, proj) {
								
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.HITBOX)) {
							
							Vector2 velo = new Vector2(fixB.getEntity().body.getLinearVelocity().x, fixB.getEntity().body.getLinearVelocity().y);
							new MomentumPickup(state, world2, camera2, rays2, 
									(int)fixB.getEntity().body.getPosition().x * 32, 
									(int)fixB.getEntity().body.getPosition().y * 32 + 32, 
									velo);
							fixB.getEntity().body.setLinearVelocity(new Vector2(0, 0));
						}
					}
				}
			});		
			
			return null;
		}
		
	};
	
	public MomentumStopper(HadalEntity user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}
	
	public void mouseClicked(PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
		clipLeft = 1;
		super.mouseClicked(state, shooter, faction, x, y, world, camera, rays);
	}

}
