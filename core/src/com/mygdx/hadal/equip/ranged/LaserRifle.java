package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

public class LaserRifle extends RangedWeapon {

	private final static String name = "Laser Rifle";
	private final static int clipSize = 12;
	private final static int ammoSize = 60;
	private final static float shootCd = 0.2f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 14.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 12.0f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 24;
	private final static float lifespan = 0.25f;
	
	private final static Sprite[] projSprites = {Sprite.ORB_BLUE, Sprite.ORB_ORANGE, Sprite.ORB_PINK, Sprite.ORB_YELLOW, Sprite.ORB_RED};

	private final static Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private final static Sprite eventSprite = Sprite.P_LASERRIFLE;
	
	private float shortestFraction;
	
	public LaserRifle(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, 0);
	}

	private Vector2 endPt = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, final Vector2 startVelocity, short filter) {
		final Equipable tool = this;
		
		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = projectileWidth * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		
		endPt.set(user.getPosition()).add(startVelocity.nor().scl(distance));
		shortestFraction = 1.0f;
		
		//Raycast length of distance until we hit a wall
		if (user.getPosition().x != endPt.x || user.getPosition().y != endPt.y) {

			state.getWorld().rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
					
					if (fixture.getFilterData().categoryBits == (short)Constants.BIT_WALL) {
						if (fraction < shortestFraction) {
							shortestFraction = fraction;
							return fraction;
						}
					}
					return -1.0f;
				}
				
			}, user.getPosition(), endPt);
		}
		
		int randomIndex = GameStateManager.generator.nextInt(projSprites.length);
		Sprite projSprite = projSprites[randomIndex];
		
		//Create Hitbox from position to wall using raycast distance. Set angle and position of hitbox and make it static.
		Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2((distance * shortestFraction * PPM), projectileHeight), lifespan, new Vector2(0, 0), filter, true, true, user, projSprite) {
			
			Vector2 newPosition = new Vector2(0, 0);
			
			@Override
			public void create() {
				super.create();

				//Rotate hitbox to match angle of fire.
				float newAngle = (float)(Math.atan2(startVelocity.y , startVelocity.x));
				newPosition.set(getPosition()).add(startVelocity.nor().scl(size.x / 2 / PPM));
				setTransform(newPosition.x, newPosition.y, newAngle);
				
				this.body.setType(BodyDef.BodyType.StaticBody);
			}
			
			@Override
			public void render(SpriteBatch batch) {
				
				//transparency changes to make hbox fade over time
				float transparency = Math.max(0, this.getLifeSpan() / this.getMaxLifespan());

				batch.setColor(1f, 1f, 1f, transparency);
				
				batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
						getPixelPosition().x - size.x / 2, 
						getPixelPosition().y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, 1, 1, 
						(float) Math.toDegrees(getOrientation()) + 180);
				
				batch.setColor(1f, 1f, 1f, 1);
			}
		};
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData(), false));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStaticStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					
					//hard coded way of making a static hbox apply knockback
					fixB.receiveDamage(0, startVelocity.nor().scl(knockback), creator, tool, false);
				}
			}
		});
	}
}
