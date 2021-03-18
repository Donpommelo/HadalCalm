package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class LoveBow extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 50;
	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.6f;
	private static final int reloadAmount = 0;
	private static final float recoil = 5.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 15.0f;
	private static final Vector2 projectileSize = new Vector2(60, 21);
	private static final float lifespan = 2.0f;
	
	private static final Sprite projSprite = Sprite.ARROW;
	private static final Sprite weaponSprite = Sprite.MT_SPEARGUN;
	private static final Sprite eventSprite = Sprite.P_SPEARGUN;
	
	private static final float baseHeal = 15.0f;
	private static final float maxCharge = 0.25f;
	private static final float projectileMaxSpeed = 65.0f;
	private static final float selfHitDelay = 0.1f;
	
	private static final float minDamage = 25.0f;
	private static final float maxDamage = 60.0f;
	
	private SoundEntity chargeSound;

	public LoveBow(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, shooter, faction, mousePosition);

		if (reloading || getClipLeft() == 0) {
			if (chargeSound != null) {
				chargeSound.turnOff();
			}
			return;
		}
		
		charging = true;
		
		if (chargeCd == 0) {
			chargeSound = new SoundEntity(state, user, SoundEffect.BOW_STRETCH, 1.0f, 1.0f, true, true, soundSyncType.TICKSYNC);
		}
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
		} else {
			if (chargeSound != null) {
				chargeSound.turnOff();
			}
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		charging = false;
		chargeCd = 0;
		
		if (chargeSound != null) {
			chargeSound.turnOff();
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.BOW_SHOOT.playUniversal(state, startPosition, 0.6f, false);

		//velocity scales to the charge percent
		float velocity = chargeCd / getChargeTime() * (projectileMaxSpeed - projectileSpeed) + projectileSpeed;
		float damage = chargeCd / getChargeTime() * (maxDamage - minDamage) + minDamage;
		
		Hitbox hurtbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity), filter, false, true, user, projSprite);
		hurtbox.setGravity(1.0f);
		
		hurtbox.addStrategy(new ControllerDefault(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new AdjustAngle(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new ContactWallDie(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new ContactUnitLoseDurability(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new DieParticles(state, hurtbox, user.getBodyData(), Particle.ARROW_BREAK));
		hurtbox.addStrategy(new DamageStandard(state, hurtbox, user.getBodyData(), damage, knockback, DamageTypes.POKING, DamageTypes.RANGED));
		hurtbox.addStrategy(new ContactUnitSound(state, hurtbox, user.getBodyData(), SoundEffect.SLASH, 0.4f, true));
		hurtbox.addStrategy(new CreateParticles(state, hurtbox, user.getBodyData(), Particle.REGEN, 0.0f, 1.0f));
		
		Hitbox healbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity), (short) 0, false, false, user, Sprite.NOTHING);
		healbox.setSyncDefault(false);
		
		healbox.addStrategy(new ControllerDefault(state, healbox, user.getBodyData()));
		healbox.addStrategy(new FixedToEntity(state, healbox, user.getBodyData(), hurtbox, new Vector2(), new Vector2(), true));
		healbox.addStrategy(new HitboxStrategy(state, healbox, user.getBodyData()) {
			
			//delay exists so the projectile doesn't immediately contact the shooter
			private float delay = selfHitDelay;
			
			@Override
			public void controller(float delta) {
				if (delay >= 0) {
					delay -= delta;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					//if shooting self after delay or any ally, the arrow will heal. Otherwise, damage is inflicted
					if (fixB.getType().equals(UserDataTypes.BODY)) {
						if ((fixB == user.getBodyData() && delay <= 0) || (fixB != user.getBodyData() && ((BodyData) fixB).getSchmuck().getHitboxfilter() == user.getHitboxfilter())) {
							((BodyData) fixB).regainHp(baseHeal, creator, true);
							SoundEffect.COIN3.playUniversal(state, hbox.getPixelPosition(), 0.5f, false);
							new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.REGEN, 1.0f, true, particleSyncType.CREATESYNC);
							hurtbox.die();
						}
					}
				}
			}
		});	
	}
	
	@Override
	public void unequip(PlayState state) {
		if (chargeSound != null) {
			chargeSound.terminate();
			chargeSound = null;
		}
	}
}
