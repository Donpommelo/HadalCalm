package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class LaserRifle extends RangedWeapon {

	private final static String name = "Laser Rifle";
	private final static int clipSize = 9;
	private final static int ammoSize = 30;
	private final static float shootCd = 0.4f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 18.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 16.0f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 2000;
	private final static int projectileHeight = 48;
	private final static float lifespan = 0.1f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static Sprite[] projSprites = {Sprite.ORB_BLUE, Sprite.ORB_ORANGE, Sprite.ORB_PINK, Sprite.ORB_YELLOW, Sprite.ORB_RED};

	private final static Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private final static Sprite eventSprite = Sprite.P_LASERRIFLE;
	
	private float shortestFraction;
	private Vector2 endPt = new Vector2(0, 0);
	
	public LaserRifle(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}

	@Override
	public void fire(PlayState state, Schmuck user, final Vector2 startVelocity, float x, float y, short filter) {
		final Equipable tool = this;
		endPt = new Vector2(user.getPosition()).add(startVelocity.nor().scl(projectileWidth));
		shortestFraction = 1.0f;
		
		if (user.getPosition().x != endPt.x || user.getPosition().y != endPt.y) {

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
				
			}, user.getPosition(), endPt);
		}
		
		int randomIndex = GameStateManager.generator.nextInt(projSprites.length);
		Sprite projSprite = projSprites[randomIndex];
		
		Hitbox hbox = new HitboxSprite(state, x, y, (int) (projectileWidth * shortestFraction * 2 * PPM + 100), projectileHeight, gravity, 
				lifespan, projDura, 0, new Vector2(0, 0), filter, true, true, user, projSprite) {
			
			@Override
			public void create() {
				super.create();
				
				//Rotate hitbox to match angle of fire.
				float newAngle = (float)(Math.atan2(startVelocity.y , startVelocity.x));
				Vector2 newPosition = getPosition().add(startVelocity.nor().scl(width / 2 / PPM));
				this.body.setTransform(newPosition.x, newPosition.y, newAngle);
			}
		};
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData(), false));
		hbox.addStrategy(new HitboxStaticStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					fixB.receiveDamage(0, startVelocity.nor().scl(knockback), creator, tool, false);
				}
			}
		});
	}
}
