package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageConstant;
import com.mygdx.hadal.strategies.hitbox.Static;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

public class Screecher extends RangedWeapon {

	private final static int clipSize = 40;
	private final static int ammoSize = 160;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 1.5f;
	private final static float knockback = 6.0f;
	private final static float projectileSpeed = 10.0f;
	private final static int range = 24;
	private final static Vector2 projectileSize = new Vector2(120, 120);
	private final static float lifespan = 0.5f;
	private final static int spread = 1;
	
	private final static Sprite projSprite = Sprite.IMPACT;

	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private SoundEntity screechSound;
	private final static float flashDuration = 0.1f;
	private final static Vector2 flashSize = new Vector2(75, 75);

	private float shortestFraction;
	
	public Screecher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		if (reloading || getClipLeft() == 0) {
			if (screechSound != null) {
				screechSound.turnOff();
			}
			return;
		}
		
		if (screechSound == null) {
			screechSound = new SoundEntity(state, user, SoundEffect.BEAM3, 0.8f, true, true, soundSyncType.TICKSYNC);
		} else {
			screechSound.turnOn();
		}
	}

	private Vector2 endPt = new Vector2();
	private Vector2 newPosition = new Vector2();
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, final Vector2 startVelocity, final short filter) {
		
		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = range * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		
		endPt.set(user.getPosition()).add(new Vector2(startVelocity).nor().scl(distance));
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
					} else {
						if (fixture.getUserData() instanceof HadalData) {
							if (fixture.getUserData() instanceof BodyData && fraction < shortestFraction) {
								if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != filter) {
									shortestFraction = fraction;
									return fraction;
								}
							} 
						} 
					}
					return -1.0f;
				}
				
			}, user.getPosition(), endPt);
		}
		
		//create explosions around the point we raycasted towards
		newPosition.set(user.getPixelPosition()).add(new Vector2(startVelocity).nor().scl(distance * shortestFraction * PPM));
		newPosition.add(ThreadLocalRandom.current().nextInt(-spread, spread + 1), ThreadLocalRandom.current().nextInt(-spread, spread + 1));
		
		Hitbox hbox = new RangedHitbox(state, newPosition, projectileSize, lifespan, new Vector2(), filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageConstant(state, hbox, user.getBodyData(), baseDamage, new Vector2(startVelocity).nor().scl(knockback), DamageTypes.SOUND, DamageTypes.RANGED));
		
		Hitbox flash = new RangedHitbox(state, startPosition, flashSize, flashDuration, new Vector2(), filter, true, true, user, projSprite);
		
		flash.addStrategy(new ControllerDefault(state, flash, user.getBodyData()));
		flash.addStrategy(new Static(state, flash, user.getBodyData()));
	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		if (screechSound != null) {
			screechSound.turnOff();
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		if (screechSound != null) {
			screechSound.terminate();
			screechSound = null;
		}
	}
}
