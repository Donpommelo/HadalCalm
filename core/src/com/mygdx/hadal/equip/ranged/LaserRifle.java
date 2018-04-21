package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class LaserRifle extends RangedWeapon {

	private final static String name = "Laser Rifle";
	private final static int clipSize = 12;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 15.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 7.5f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 2000;
	private final static int projectileHeight = 24;
	private final static float lifespan = 0.25f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_orange";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		float shortestFraction;
		
		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Vector2 startVelocity, float x, float y, short filter) {
			Vector2 endPt = new Vector2(user.getBody().getPosition()).add(startVelocity.nor().scl(projectileWidth));
			
			shortestFraction = 1.0f;
			
			if (user.getBody().getPosition().x != endPt.x || user.getBody().getPosition().y != endPt.y) {

				state.getWorld().rayCast(new RayCastCallback() {

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
			
			Hitbox hbox = new HitboxImage(state, x, y, (int) (projectileWidth * shortestFraction * 2 * PPM + 100), projectileHeight, gravity, 
					lifespan, projDura, 0, new Vector2(0, 0), filter, true, user, projSpriteId) {
				
				@Override
				public void create() {
					super.create();
					
					//Rotate hitbox to match angle of fire.
					float newAngle = (float)(Math.atan2(startVelocity.y , startVelocity.x));
					Vector2 newPosition = this.body.getPosition().add(startVelocity.nor().scl(width / 2 / PPM));
					this.body.setTransform(newPosition.x, newPosition.y, newAngle);
				}
			};
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData(), false));
			hbox.addStrategy(new HitboxStaticStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		}
	};
	
	public LaserRifle(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
