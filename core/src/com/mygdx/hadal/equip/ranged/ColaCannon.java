package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class ColaCannon extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 13;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 10.0f;
	private final static float recoil = 18.0f;
	private final static float knockback = 7.5f;
	private final static float projectileSpeed = 50.0f;
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 2.0f;

	private final static float procCd = .05f;
	private final static float fireDuration = 2.0f;
	private final static float veloDeprec = 1.0f;
	private final static float minVelo = 9.0f;
	private final static float minDuration = 0.5f;

	private final static Sprite projSprite = Sprite.COLA;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float maxCharge = 8000.0f;
	private final static float noiseThreshold = 2000.0f;

	private Vector2 lastMouse = new Vector2();
	private float lastNoise;

	public ColaCannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);

		if (reloading || getClipLeft() == 0) {
			return;
		}
		
		charging = true;
		
		//While held, gain charge equal to mouse movement from location last update
		if (chargeCd < getChargeTime()) {
			chargeCd += lastMouse.dst(mouseLocation);
			if (chargeCd >= getChargeTime()) {
				chargeCd = getChargeTime();
			}
			
			if (chargeCd > lastNoise + noiseThreshold) {
				lastNoise += noiseThreshold;
				SoundEffect.SHAKE.playUniversal(state, user.getPixelPosition(), 1.0f, false);
			}
		}
		
		lastMouse.set(mouseLocation);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		
		//when released, spray weapon at mouse. Spray duration and velocity scale to charge
		if (processClip(state, bodyData)) {
			SoundEffect.POPTAB.playUniversal(state, user.getPixelPosition(), 1.0f, false);

			final float duration = fireDuration * chargeCd / getChargeTime() + minDuration;
			final float velocity = projectileSpeed * chargeCd / getChargeTime() + minVelo;
			
			bodyData.addStatus(new FiringWeapon(state, duration, bodyData, bodyData, velocity, minVelo, veloDeprec, projectileSize.x, procCd, this));
			
			charging = false;
			chargeCd = 0;
			lastNoise = 0;
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.COLA_IMPACT));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.COLA, DamageTypes.WATER, DamageTypes.RANGED));
	}
}
