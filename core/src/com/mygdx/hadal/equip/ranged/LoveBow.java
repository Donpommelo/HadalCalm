package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

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

	private static final float minHeal = 15.0f;
	private static final float maxHeal = 35.0f;
	private static final float maxCharge = 0.25f;
	private static final float projectileMaxSpeed = 65.0f;
	private static final float selfHitDelay = 0.1f;
	
	private static final float minDamage = 34.0f;
	private static final float maxDamage = 69.0f;
	
	private SoundEntity chargeSound;

	public LoveBow(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
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
			chargeSound = new SoundEntity(state, user, SoundEffect.BOW_STRETCH, 0.0f, 1.0f, 1.0f,
					true, true, SyncType.TICKSYNC);
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
		float charge = chargeCd / getChargeTime();
		SyncedAttack.LOVE_ARROW.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, charge);
	}

	public static Hitbox createLoveArrow(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] charge) {
		SoundEffect.BOW_SHOOT.playSourced(state, startPosition, 0.6f);
		user.recoil(startVelocity, recoil);

		float chargeAmount = 0.0f;
		if (charge.length > 0) {
			chargeAmount = charge[0];
		}

		//velocity scales to the charge percent
		float velocity = chargeAmount * (projectileMaxSpeed - projectileSpeed) + projectileSpeed;
		float damage = chargeAmount * (maxDamage - minDamage) + minDamage;
		float heal = chargeAmount * (maxHeal - minHeal) + minHeal;

		Hitbox hurtbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity),
				user.getHitboxfilter(), false, true, user, projSprite);
		hurtbox.setGravity(1.0f);

		hurtbox.addStrategy(new ControllerDefault(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new AdjustAngle(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new ContactWallDie(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new ContactUnitLoseDurability(state, hurtbox, user.getBodyData()));
		hurtbox.addStrategy(new DieParticles(state, hurtbox, user.getBodyData(), Particle.ARROW_BREAK).setSyncType(SyncType.NOSYNC));
		hurtbox.addStrategy(new DamageStandard(state, hurtbox, user.getBodyData(), damage, knockback, DamageSource.LOVE_BOW,
				DamageTag.POKING, DamageTag.RANGED));
		hurtbox.addStrategy(new ContactUnitSound(state, hurtbox, user.getBodyData(), SoundEffect.SLASH, 0.4f, true).setSynced(false));
		hurtbox.addStrategy(new ContactWallSound(state, hurtbox, user.getBodyData(), SoundEffect.BULLET_DIRT_HIT, 0.8f).setSynced(false));
		hurtbox.addStrategy(new CreateParticles(state, hurtbox, user.getBodyData(), Particle.BOW_TRAIL, 0.0f, 1.0f)
				.setRotate(true).setSyncType(SyncType.NOSYNC));

		Hitbox healbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity),
				(short) 0, false, false, user, Sprite.NOTHING);
		healbox.setSyncDefault(false);

		healbox.addStrategy(new ControllerDefault(state, healbox, user.getBodyData()));
		healbox.addStrategy(new FixedToEntity(state, healbox, user.getBodyData(), hurtbox, new Vector2(), new Vector2()).setRotate(true));
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
					if (UserDataType.BODY.equals(fixB.getType())) {

						if ((fixB == user.getBodyData() && delay <= 0) || (fixB != user.getBodyData() && ((BodyData) fixB).getSchmuck().getHitboxfilter() == user.getHitboxfilter())) {
							((BodyData) fixB).regainHp(heal, creator, true);
							SoundEffect.COIN3.playUniversal(state, hbox.getPixelPosition(), 0.5f, false);
							ParticleEntity heal = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.BOW_HEAL, 1.0f,
									true, SyncType.NOSYNC);
							if (!state.isServer()) {
								((ClientState) state).addEntity(heal.getEntityID(), heal, false, ClientState.ObjectLayer.HBOX);
							}
							hurtbox.die();
						} else if (((BodyData) fixB).getSchmuck().getHitboxfilter() != user.getHitboxfilter()) {
							ParticleEntity hurt = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.BOW_HURT, 1.0f,
									true, SyncType.NOSYNC);
							if (!state.isServer()) {
								((ClientState) state).addEntity(hurt.getEntityID(), hurt, false, ClientState.ObjectLayer.HBOX);
							}
						}
					}
				}
			}
		});
		if (!state.isServer()) {
			((ClientState) state).addEntity(healbox.getEntityID(), healbox, false, ClientState.ObjectLayer.HBOX);
		}
		return hurtbox;
	}

	@Override
	public void unequip(PlayState state) {
		if (chargeSound != null) {
			chargeSound.terminate();
			chargeSound = null;
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) minDamage),
				String.valueOf((int) maxDamage),
				String.valueOf((int) minHeal),
				String.valueOf((int) maxDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(maxCharge)};
	}
}
