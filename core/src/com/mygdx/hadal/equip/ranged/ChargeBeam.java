package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class ChargeBeam extends RangedWeapon {

	private final static int clipSize = 4;
	private final static int ammoSize = 16;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.3f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 15.0f;
	private final static float recoil = 7.5f;
	private final static float knockback = 10.0f;
	private final static float projectileSpeed = 60.0f;
	private final static Vector2 projectileSize = new Vector2(28, 28);
	private final static float lifespan = 0.6f;
	
	private final static Sprite projSprite = Sprite.CHARGE_BEAM;
	private final static Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private final static Sprite eventSprite = Sprite.P_CHARGEBEAM;
	
	private static final float maxCharge = 0.5f;
	private int chargeStage = 0;
	
	private ParticleEntity charge, overcharge;
	
	public ChargeBeam(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x * 3.0f, maxCharge);
	}
	
	private Vector2 particleOrigin = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		if (reloading || getClipLeft() == 0) {
			return;
		}
		
		particleOrigin.set(weaponVelo).nor().scl(60);
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			if (charge == null) {
				charge = new ParticleEntity(user.getState(), user, Particle.CHARGING, 0.0f, 0.0f, false, particleSyncType.TICKSYNC);
				charge.setScale(0.5f);
			}
			charge.setOffset(particleOrigin);
			charge.turnOn();

			chargeCd += delta;
			if (chargeCd >= getChargeTime()) {
				chargeCd = getChargeTime();
			}
		} else {
			if (overcharge == null) {
				overcharge = new ParticleEntity(user.getState(), user, Particle.OVERCHARGE, 0.0f, 0.0f, false, particleSyncType.TICKSYNC);
				overcharge.setScale(0.5f);
			}
			if (charge != null) {
				charge.turnOff();
			}
			overcharge.setOffset(particleOrigin);
			overcharge.turnOn();
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		charging = false;
		chargeCd = 0;
		
		if (charge != null) {
			charge.turnOff();
		}
		if (overcharge != null) {
			overcharge.turnOff();
		}
	}
	
	@Override
	public boolean reload(float delta) {
		boolean finished = super.reload(delta);
		
		if (charge != null) {
			charge.turnOff();
		}
		if (overcharge != null) {
			overcharge.turnOff();
		}
		
		return finished;
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LASERHARPOON.playUniversal(state, startPosition, 1.0f, false);

		//power of hitbox scales to the amount charged
		if (chargeCd >= getChargeTime()) {
			chargeStage = 2;
		}
		else if (chargeCd >= getChargeTime() / 2) {
			chargeStage = 1;
		} else {
			chargeStage = 0;
		}
		
		float sizeMultiplier = 1.0f;
		float damageMultiplier = 2.5f;
		float kbMultiplier = 1;

		switch(chargeStage) {
		case 2:
			sizeMultiplier = 1.8f;
			damageMultiplier = 5.0f;
			kbMultiplier = 3.0f;
			break;
		case 1:
			sizeMultiplier = 1.2f;
			damageMultiplier = 3.5f;
			kbMultiplier = 2.0f;
			break;
		}
		
		final float damageMultiplier2 = damageMultiplier;
		final float kbMultiplier2 = kbMultiplier;
		
		Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(projectileSize).scl(sizeMultiplier), lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setDurability(3);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					fixB.receiveDamage(baseDamage * damageMultiplier2, this.hbox.getLinearVelocity().nor().scl(knockback * kbMultiplier2), user.getBodyData(), true, DamageTypes.ENERGY, DamageTypes.RANGED);
				}
			}
		});
		
		if (chargeStage == 2) {
			hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.OVERCHARGE, 0.0f, 3.0f));
			hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.OVERCHARGE, 0.4f));
		}
	}
	
	@Override
	public void unequip() {
		if (charge != null) {
			charge.queueDeletion();
			charge = null;
		}
		if (overcharge != null) {
			overcharge.queueDeletion();
			overcharge = null;
		}
	}
}
