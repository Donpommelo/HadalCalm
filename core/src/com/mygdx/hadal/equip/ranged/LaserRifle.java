package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageConstant;
import com.mygdx.hadal.strategies.hitbox.Static;
import com.mygdx.hadal.strategies.hitbox.TravelDistanceDie;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

public class LaserRifle extends RangedWeapon {

	private final static int clipSize = 8;
	private final static int ammoSize = 56;
	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 22.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 12.0f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 30;
	private final static float lifespan = 0.25f;
	
	private final static Sprite[] projSprites = {Sprite.LASER_BEAM, Sprite.LASER_BEAM, Sprite.LASER_BEAM, Sprite.LASER_BEAM, Sprite.LASER_BEAM};

	private final static Vector2 trailSize = new Vector2(10, 10);
	private final static float trailSpeed = 60.0f;
	private final static float trailLifespan = 3.0f;

	private final static Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private final static Sprite eventSprite = Sprite.P_LASERRIFLE;
	
	private float shortestFraction;
	
	public LaserRifle(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, 0);
	}

	private Vector2 endPt = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, final Vector2 startVelocity, short filter) {
		SoundEffect.LASER2.playUniversal(state, startPosition, 1.0f, false);

		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = projectileWidth * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		
		endPt.set(user.getPosition()).add(new Vector2(startVelocity).nor().scl(distance));
		shortestFraction = 1.0f;
		
		//Raycast length of distance until we hit a wall
		if (user.getPosition().x != endPt.x || user.getPosition().y != endPt.y) {

			state.getWorld().rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
					
					if (fixture.getFilterData().categoryBits == (short) Constants.BIT_WALL) {
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
			
			Vector2 newPosition = new Vector2();
			
			@Override
			public void create() {
				super.create();
				//Rotate hitbox to match angle of fire.
				float newAngle = (float)(Math.atan2(startVelocity.y , startVelocity.x));
				newPosition.set(getPosition()).add(new Vector2(startVelocity).nor().scl(size.x / 2 / PPM));
				setTransform(newPosition.x, newPosition.y, newAngle);
			}
			
			@Override
			public boolean isVisible() {
				return true;
			}
		};
		
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageConstant(state, hbox, user.getBodyData(), baseDamage, new Vector2(startVelocity).nor().scl(knockback), DamageTypes.ENERGY, DamageTypes.RANGED));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setDrawOnSelf(false));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		
		Hitbox trail = new RangedHitbox(state, user.getPixelPosition(), trailSize, trailLifespan, startVelocity.nor().scl(trailSpeed), filter, true, false, user, projSprite);
		
		trail.addStrategy(new ControllerDefault(state, trail, user.getBodyData()));
		trail.addStrategy(new TravelDistanceDie(state, trail, user.getBodyData(), distance * shortestFraction));
		trail.addStrategy(new CreateParticles(state, trail, user.getBodyData(), Particle.LASER_TRAIL, 0.0f, 3.0f));
	}
}
