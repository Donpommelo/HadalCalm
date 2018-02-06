package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class SlodgeGun extends RangedWeapon {

	private final static String name = "Slodge Gun";
	private final static int clipSize = 1;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 40;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static float slowDura = 3.0f;

	private final static int explosionRadius = 250;

	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "debris_c";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, final short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, world, camera, rays, user, projSpriteId);
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
							explode = true;
						}
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					} else {
						explode = true;
					}
					if (explode) {

						Hitbox explosion = new Hitbox(state, 
								this.hbox.getBody().getPosition().x * PPM , 
								this.hbox.getBody().getPosition().y * PPM,	
								explosionRadius, explosionRadius, 0, .02f, 1, 0, new Vector2(0, 0),
								filter, true, world, camera2, rays2, user);
						
						explosion.setUserData(new HitboxData(state, world, explosion) {
							
							@Override
							public void onHit(HadalData fixB) {
								if (fixB != null) {
									if (fixB instanceof BodyData) {
										((BodyData)fixB).addStatus(new Slodged(state, world, camera2, rays2, slowDura, user.getBodyData(), ((BodyData)fixB), 50));
									}
								}
							}
						});
						
						
						hbox.queueDeletion();
					}
					
				}
			});		
		}
	};
	
	public SlodgeGun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}
}
