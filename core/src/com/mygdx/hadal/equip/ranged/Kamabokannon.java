package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class Kamabokannon extends RangedWeapon {

	private static final int clipSize = 100;
	private static final int ammoSize = 500;
	private static final float shootCd = 0.08f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.8f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 13.0f;
	private static final float recoil = 3.0f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 30.0f;
	private static final Vector2 projectileSize = new Vector2(60, 50);
	private static final float lifespan = 2.0f;
	
	private static final Sprite weaponSprite = Sprite.MT_BOILER;
	private static final Sprite eventSprite = Sprite.P_BOILER;
	
	private static final float maxCharge = 0.25f;
	private static final float lerpSpeed = 0.2f;

	private SoundEntity oozeSound;
	
	public Kamabokannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	private float controllerCount = 0;
	private static final float pushInterval = 1 / 60f;
	private final Vector2 aimPointer = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		controllerCount += delta;

		if (reloading || getClipLeft() == 0) {
			if (oozeSound != null) {
				oozeSound.turnOff();
			}
			return;
		}
		
		//while held, lerp towards mouse pointer
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			super.mouseClicked(delta, state, shooter, faction, mouseLocation);
			weaponVelo.setAngle(MathUtils.lerpAngleDeg(aimPointer.angle(), weaponVelo.angle(), lerpSpeed));
			aimPointer.set(weaponVelo);
		}
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			chargeCd += (delta + shootCd);
			
			if (chargeCd >= getChargeTime()) {
				super.mouseClicked(delta, state, shooter, faction, mouseLocation);
				
				aimPointer.set(weaponVelo);
				
				if (oozeSound == null) {
					oozeSound = new SoundEntity(state, user, SoundEffect.OOZE, 0.8f, 1.0f, true, true, soundSyncType.TICKSYNC);
				} else {
					oozeSound.turnOn();
				}
			}
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (chargeCd >= getChargeTime()) {
			chargeCd = getChargeTime();
			super.execute(state, shooter);
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 1.0f));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_IMPACT));
	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		chargeCd = 0;
		charging = false;
		
		if (oozeSound != null) {
			oozeSound.turnOff();
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		if (oozeSound != null) {
			oozeSound.terminate();
			oozeSound = null;
		}
	}
}
