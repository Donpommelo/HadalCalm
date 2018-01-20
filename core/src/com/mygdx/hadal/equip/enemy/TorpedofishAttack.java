package com.mygdx.hadal.equip.enemy;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HitboxImage;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class TorpedofishAttack extends RangedWeapon {

	private final static String name = "Torpedofish Torpedo";
	private final static int clipSize = 1;
	private final static float shootCd = 2.5f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.2f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 5.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.5f;
	private final static float projectileSpeed = 12.0f;
	private final static int projectileWidth = 45;
	private final static int projectileHeight = 45;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 20.0f;
	private final static float explosionKnockback = 4.0f;
	
	private final static String spriteId = "orb_red";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, projectileSpeed, startVelocity,
					filter, false, world, camera, rays, user, spriteId);
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							explode = true;
						}
						
					} else {
						explode = true;
					}
					if (explode) {
						explode(state, this.hbox.getBody().getPosition().x * PPM , this.hbox.getBody().getPosition().y * PPM, 
								world2, camera2, rays2, user);
						hbox.queueDeletion();
					}
					
				}
			});		
		}
		
		public void explode(PlayState state, float x, float y, World world, OrthographicCamera camera, RayHandler rays, 
				final Schmuck user) {
			Hitbox explosion = new Hitbox(state, 
					x, y,	explosionRadius, explosionRadius, 0, .02f, 1, 0, new Vector2(0, 0),
					(short) 0, true, world, camera, rays, user);

				explosion.setUserData(new HitboxData(state, world, explosion){
					public void onHit(HadalData fixB) {
						if (fixB != null) {
							fixB.receiveDamage(explosionDamage, this.hbox.getBody().getLinearVelocity().nor().scl(explosionKnockback), 
									user.getBodyData(), true, DamageTypes.EXPLOSIVE);
						}
					}
				});
		}
		
	};
	
	

	public TorpedofishAttack(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
