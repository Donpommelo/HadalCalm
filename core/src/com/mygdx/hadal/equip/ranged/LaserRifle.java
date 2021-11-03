package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PPM;

public class LaserRifle extends RangedWeapon {

	private static final int clipSize = 8;
	private static final int ammoSize = 56;
	private static final float shootCd = 0.4f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.25f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 24.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 12.0f;
	private static final float projectileSpeed = 20.0f;
	private static final int projectileWidth = 40;
	private static final int projectileHeight = 30;
	private static final float lifespan = 0.25f;
	
	private static final Sprite projSprite = Sprite.LASER_BEAM;

	private static final Vector2 trailSize = new Vector2(30, 30);
	private static final float trailSpeed = 120.0f;
	private static final float trailLifespan = 3.0f;

	private static final Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private static final Sprite eventSprite = Sprite.P_LASERRIFLE;
	
	private float shortestFraction;
	
	public LaserRifle(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, lifespan, 0);
	}

	private final Vector2 endPt = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LASER2.playUniversal(state, startPosition, 0.8f, false);

		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = projectileWidth * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		entityLocation.set(user.getPosition());
		endPt.set(entityLocation).add(new Vector2(startVelocity).nor().scl(distance));
		shortestFraction = 1.0f;
		
		//Raycast length of distance until we hit a wall
		if (entityLocation.x != endPt.x || entityLocation.y != endPt.y) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {

				if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
					if (fraction < shortestFraction) {
						shortestFraction = fraction;
						return fraction;
					}
				}
				return -1.0f;
			}, entityLocation, endPt);
		}

		//Create Hitbox from position to wall using raycast distance. Set angle and position of hitbox and make it static.
		Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2((distance * shortestFraction * PPM), projectileHeight), lifespan, new Vector2(), filter, true, true, user, projSprite) {

			private final Vector2 newPosition = new Vector2();
			
			@Override
			public void create() {
				super.create();
				
				//this makes the laser hbox's lifespan unmodifiable
				setLifeSpan(lifespan);

				//Rotate hitbox to match angle of fire.
				float newAngle = MathUtils.atan2(startVelocity.y , startVelocity.x);
				newPosition.set(getPosition()).add(new Vector2(startVelocity).nor().scl(size.x / 2 / PPM));
				setTransform(newPosition.x, newPosition.y, newAngle);
			}
		};
		hbox.setEffectsVisual(false);
		hbox.setEffectsMovement(false);
		hbox.makeUnreflectable();

		hbox.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED)
		.setConstantKnockback(true, startVelocity));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setDrawOnSelf(false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.6f, true));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		
		//the trail creates particles along the projectile's length
		Hitbox trail = new RangedHitbox(state, user.getPixelPosition(), trailSize, trailLifespan, startVelocity.nor().scl(trailSpeed), filter, true, true, user, projSprite);
		trail.setEffectsHit(false);
		trail.setEffectsMovement(false);
		trail.makeUnreflectable();

		trail.setPassability((short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

		trail.addStrategy(new ControllerDefault(state, trail, user.getBodyData()));
		trail.addStrategy(new TravelDistanceDie(state, trail, user.getBodyData(), distance * shortestFraction));
		trail.addStrategy(new CreateParticles(state, trail, user.getBodyData(), Particle.LASER_TRAIL, 0.0f, 1.0f));
	}

	@Override
	public float getBotRangeMax() { return projectileWidth; }
}
