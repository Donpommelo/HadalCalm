package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
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
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Static;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

import java.util.concurrent.ThreadLocalRandom;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Screecher extends RangedWeapon {

	private static final int clipSize = 40;
	private static final int ammoSize = 160;
	private static final float shootCd = 0.1f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.2f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 8.0f;
	private static final float recoil = 1.5f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 10.0f;
	private static final int range = 24;
	private static final Vector2 projectileSize = new Vector2(120, 120);
	private static final float lifespan = 0.5f;
	private static final int spread = 1;
	
	private static final Sprite projSprite = Sprite.IMPACT;

	private static final Sprite weaponSprite = Sprite.MT_DEFAULT;
	private static final Sprite eventSprite = Sprite.P_DEFAULT;
	
	private SoundEntity screechSound;
	private static final float flashDuration = 0.1f;
	private static final Vector2 flashSize = new Vector2(75, 75);

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
			screechSound = new SoundEntity(state, user, SoundEffect.BEAM3, 0.8f, 1.0f, true, true, soundSyncType.TICKSYNC);
		} else {
			screechSound.turnOn();
		}
	}

	private final Vector2 endPt = new Vector2();
	private final Vector2 newPosition = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = range * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		
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
			}, entityLocation, endPt);
		}
		
		//create explosions around the point we raycast towards
		newPosition.set(user.getPixelPosition()).add(new Vector2(startVelocity).nor().scl(distance * shortestFraction * PPM));
		newPosition.add(ThreadLocalRandom.current().nextInt(-spread, spread + 1), ThreadLocalRandom.current().nextInt(-spread, spread + 1));
		
		Hitbox hbox = new RangedHitbox(state, newPosition, projectileSize, lifespan, new Vector2(), filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.SOUND, DamageTypes.RANGED)
		.setConstantKnockback(true, startVelocity));
		
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
