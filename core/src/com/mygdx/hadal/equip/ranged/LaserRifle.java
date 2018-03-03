package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.HitboxFactory;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class LaserRifle extends RangedWeapon {

	private final static String name = "Laser Rifle";
	private final static int clipSize = 6;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 7.5f;
	private final static float knockback = 12.5f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 2000;
	private final static int projectileHeight = 24;
	private final static float lifespan = 0.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_orange";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		float shortestFraction;
		
		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			Vector2 endPt = new Vector2(user.getBody().getPosition()).add(startVelocity.nor().scl(projectileWidth));
			
			shortestFraction = 1.0f;
			
			if (user.getBody().getPosition().x != endPt.x || user.getBody().getPosition().y != endPt.y) {

				world.rayCast(new RayCastCallback() {

					@Override
					public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
						if (fixture.getUserData() == null) {
							if (fraction < shortestFraction) {
								shortestFraction = fraction;
								return fraction;
							}
						} else {

							if (fixture.getUserData() instanceof HadalData) {

								if (((HadalData)fixture.getUserData()).getType() == UserDataTypes.WALL && 
										fraction < shortestFraction) {
									shortestFraction = fraction;

									return fraction;
								}
							}
						}
						return -1.0f;
					}
					
				}, user.getBody().getPosition(), endPt);
			}
			

			HitboxImage proj = new HitboxImage(state, x, y, (int) (projectileWidth * shortestFraction * 2 * PPM), projectileHeight, gravity, 
					lifespan, projDura, 0, new Vector2(0, 0), filter, true, world, camera, rays, user, projSpriteId) {
				
				@Override
				public void create() {
					this.body = BodyBuilder.createBox(world, startX, startY, width / 2, height / 2, grav, 0.0f, 0, 0, true, false, Constants.BIT_PROJECTILE, 
							(short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR),
							filter, true, data);
					
					//Rotate hitbox to match angle of fire.
					float newAngle = (float)(Math.atan2(startVelocity.y , startVelocity.x));
					Vector2 newPosition = this.body.getPosition().add(startVelocity.nor().scl(width / 2 / PPM));
					this.body.setTransform(newPosition.x, newPosition.y, newAngle);
				}
				
				@Override
				public void controller(float delta) {
					lifeSpan -= delta;
					if (lifeSpan <= 0) {
						state.destroy(this);
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
				}
			});		
		}
	};
	
	public LaserRifle(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
