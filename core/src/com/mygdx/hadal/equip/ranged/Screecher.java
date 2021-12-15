package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Screecher extends RangedWeapon {

	private static final int clipSize = 60;
	private static final int ammoSize = 240;
	private static final float shootCd = 0.15f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.2f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 12.0f;
	private static final float recoil = 1.5f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 12.0f;
	private static final int range = 24;
	private static final Vector2 projectileSize = new Vector2(120, 120);
	private static final float lifespan = 0.3f;
	private static final int spread = 1;
	
	private static final Sprite weaponSprite = Sprite.MT_DEFAULT;
	private static final Sprite eventSprite = Sprite.P_DEFAULT;
	
	private SoundEntity screechSound;

	private static final Vector2 trailSize = new Vector2(30, 30);
	private static final float trailSpeed = 120.0f;
	private static final float trailLifespan = 3.0f;

	private float shortestFraction;
	
	public Screecher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
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
			screechSound = new SoundEntity(state, user, SoundEffect.BEAM3, 0.8f, 1.0f, true, true, SyncType.TICKSYNC);
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
		newPosition.add(MathUtils.random(-spread, spread + 1), MathUtils.random(-spread, spread + 1));
		SyncedAttack.SCREECH.initiateSyncedAttackSingle(state, user, newPosition, startVelocity,distance * shortestFraction);
	}

	public static Hitbox createScreech(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		float distance = range;
		if (extraFields.length >= 1) {
			distance = extraFields[0];
		}

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.NOTHING);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.POLYGON, 0.0f, 1.0f).setParticleColor(
				HadalColor.RANDOM).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.SOUND, DamageTypes.RANGED)
				.setConstantKnockback(true, startVelocity));

		//the trail creates particles along the projectile's length
		Hitbox trail = new RangedHitbox(state, user.getPixelPosition(), trailSize, trailLifespan, startVelocity.nor().scl(trailSpeed),
				user.getHitboxfilter(), true, true, user, Sprite.NOTHING);
		trail.setSyncDefault(false);
		trail.setEffectsHit(false);
		trail.setEffectsMovement(false);
		trail.makeUnreflectable();

		trail.setPassability((short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

		trail.addStrategy(new ControllerDefault(state, trail, user.getBodyData()));
		trail.addStrategy(new TravelDistanceDie(state, trail, user.getBodyData(), distance));
		trail.addStrategy(new CreateParticles(state, trail, user.getBodyData(), Particle.POLYGON, 0.0f, 1.0f)
				.setParticleColor(HadalColor.RANDOM).setParticleSize(60).setSyncType(SyncType.NOSYNC));

		if (!state.isServer()) {
			((ClientState) state).addEntity(trail.getEntityID(), trail, false, ClientState.ObjectSyncLayers.EFFECT);
		}
		return hbox;
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

	@Override
	public float getBotRangeMax() { return range; }
}
