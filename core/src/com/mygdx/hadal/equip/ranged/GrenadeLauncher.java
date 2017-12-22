package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class GrenadeLauncher extends RangedWeapon {

	private final static String name = "Grenade Launcher";
	private final static int clipSize = 3;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 0.5f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 5.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 10.0f;
	private final static int projectileWidth = 15;
	private final static int projectileHeight = 15;
	private final static float lifespan = 8.0f;
	private final static float gravity = 1;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 250;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 15.0f;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(HadalEntity user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			final HadalEntity user2 = user;

			Hitbox proj = new Hitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, world, camera, rays, user);
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, this.hbox.body.getLinearVelocity().nor().scl(knockback));
							explode = true;
						}
					} else {
						explode = true;
					}

					if (explode) {
						Hitbox explosion = new Hitbox(state, 
						this.hbox.body.getPosition().x * PPM , this.hbox.body.getPosition().y * PPM,
						explosionRadius, explosionRadius, 0, .02f, 1, 0, new Vector2(0, 0),
						(short) 0, true, world2, camera2, rays2, user2);

						explosion.setUserData(new HitboxData(state, world2, explosion){
							public void onHit(HadalData fixB) {
								if (fixB != null) {
									if (fixB.getType().equals(UserDataTypes.BODY)) {
										((BodyData) fixB).receiveDamage(explosionDamage, 
												new Vector2(fixB.getEntity().body.getPosition().x - this.hbox.body.getPosition().x, 
														fixB.getEntity().body.getPosition().y - this.hbox.body.getPosition().y).nor().scl(explosionKnockback));
											
									}
								}
							}
						});
					}
					
					super.onHit(fixB);
					
					
					
				}
			});		
			
			return null;
		}
		
	};
	
	public GrenadeLauncher(HadalEntity user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
