package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class MomentumShooter extends RangedWeapon {

	private final static String name = "Momentum Shooter";
	private final static int clipSize = 1;
	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.25f;
	private final static int reloadAmount = 1;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 40.0f;
	private final static int projectileWidth = 30;
	private final static int projectileHeight = 30;
	private final static float lifespan = 4.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 5;
	
	private final static float restitution = 0.0f;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			Hitbox proj = new Hitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, startVelocity,
					filter, false, world, camera, rays, user);
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (state.getPlayer().getMomentums().size > 0) {
							Vector2 velo = state.getPlayer().getMomentums().first();
							fixB.getEntity().getBody().setLinearVelocity(velo);
							
							if (fixB instanceof HitboxData) {
								((HitboxData)fixB).getHbox().setFilter((short)0);
							}
						}
					}
					super.onHit(fixB);
				}
			});		
		}
	};
	
	public MomentumShooter(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
