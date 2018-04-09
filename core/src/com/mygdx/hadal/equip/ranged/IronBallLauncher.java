package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class IronBallLauncher extends RangedWeapon {

	private final static String name = "Iron Ball Launcher";
	private final static int clipSize = 1;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 1.25f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 75.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 35.0f;
	private final static int projectileWidth = 88;
	private final static int projectileHeight = 90;
	private final static float lifespan = 2.5f;
	private final static float gravity = 10;
	
	private final static int projDura = 5;
	
	private final static float restitution = 0.5f;

	private final static String weapSpriteId = "cannon";
	private final static String projSpriteId = "iron_ball";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, startVelocity,
					filter, false, world, camera, rays, user, projSpriteId) {
				
				@Override
				public void controller(float delta) {
					lifeSpan -= delta;
					if (lifeSpan <= 0) {
							queueDeletion();
					}
				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					}
					hbox.particle.onForBurst(0.25f);
				}
			});		
		}
		
	};
	
	public IronBallLauncher(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
