package com.mygdx.hadal.equip.ranged;

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
import static com.mygdx.hadal.utils.Constants.PPM;

public class GrenadeLauncher extends RangedWeapon {

	private final static String name = "Grenade Launcher";
	private final static int clipSize = 4;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 0.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 18.0f;
	private final static int projectileWidth = 25;
	private final static int projectileHeight = 25;
	private final static float lifespan = 3.0f;
	private final static float gravity = 1;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 10.0f;

	private final static String weapSpriteId = "grenadelauncher";
	private final static String projSpriteId = "grenade";

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, false, world, camera, rays, user, projSpriteId) {
				public void controller(float delta) {
					super.controller(delta);
					if (lifeSpan <= 0) {
						explode(state, this.body.getPosition().x * PPM , this.body.getPosition().y * PPM, 
								world, camera, rays, user);
					}
				}
			};
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							explode(state, this.hbox.getBody().getPosition().x * PPM , this.hbox.getBody().getPosition().y * PPM, 
									world2, camera2, rays2, user);
							hbox.queueDeletion();
						}
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
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
						
						Vector2 kb = new Vector2(fixB.getEntity().getBody().getPosition().x - this.hbox.getBody().getPosition().x,
								fixB.getEntity().getBody().getPosition().y - this.hbox.getBody().getPosition().y);
						
						fixB.receiveDamage(explosionDamage, kb.nor().scl(explosionKnockback), 
								user.getBodyData(), true, DamageTypes.EXPLOSIVE);
					}
				}
			});
		}
	};
	
	public GrenadeLauncher(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}
}
