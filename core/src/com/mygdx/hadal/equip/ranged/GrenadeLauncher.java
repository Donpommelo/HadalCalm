package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class GrenadeLauncher extends RangedWeapon {

	private final static String name = "Grenade Launcher";
	private final static int clipSize = 5;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.5f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 13.5f;
	private final static int projectileWidth = 25;
	private final static int projectileHeight = 25;
	private final static float lifespan = 3.0f;
	private final static float gravity = 1;
	private final static float restitution = 0.3f;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 25.0f;

	private final static String weapSpriteId = "grenadelauncher";
	private final static String projSpriteId = "grenade";

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, startVelocity,
					filter, false, world, camera, rays, user, projSpriteId) {
				
				@Override
				public void controller(float delta) {
					super.controller(delta);
					if (lifeSpan <= 0) {
						WeaponUtils.explode(state, this.body.getPosition().x * PPM , this.body.getPosition().y * PPM, 
								world2, camera2, rays2, user, explosionRadius, explosionDamage, explosionKnockback, (short)0);
					}
				}
			};
			
			
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							WeaponUtils.explode(state, this.hbox.getBody().getPosition().x * PPM , this.hbox.getBody().getPosition().y * PPM, 
									world2, camera2, rays2, user, explosionRadius, explosionDamage, explosionKnockback, (short) 0);
							hbox.queueDeletion();
						}
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					}
				}
			});		
		}
	};
	
	public GrenadeLauncher(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}
}
