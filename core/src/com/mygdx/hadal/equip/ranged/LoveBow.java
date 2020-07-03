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
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class LoveBow extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 50;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 55.0f;
	private final static float recoil = 5.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 15.0f;
	private final static Vector2 projectileSize = new Vector2(80, 20);
	private final static float lifespan = 2.0f;
	
	private final static Sprite projSprite = Sprite.ARROW;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float baseHeal = 15.0f;
	private static final float maxCharge = 0.3f;
	private final static float projectileMaxSpeed = 65.0f;
	private final static float selfHitDelay = 0.15f;
	
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
			chargeSound = new SoundEntity(state, user, SoundEffect.BOW_STRETCH, 1.0f, true, true, soundSyncType.TICKSYNC);
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
		SoundEffect.BOW_SHOOT.playUniversal(state, startPosition, 0.4f, false);

		float velocity = chargeCd / getChargeTime() * (projectileMaxSpeed - projectileSpeed) + projectileSpeed;
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity), (short) 0, false, true, user, projSprite);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ARROW_BREAK));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
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
					if (fixB.getType().equals(UserDataTypes.BODY)) {
						if (((BodyData) fixB).getSchmuck().getHitboxfilter() != user.getHitboxfilter()) {
							fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), hbox.getLinearVelocity().nor().scl(knockback), creator, true, DamageTypes.RANGED, DamageTypes.POKING);
							SoundEffect.SLASH.playUniversal(state, hbox.getPixelPosition(), 0.5f, false);
							hbox.die();
						} else if (delay <= 0) {
							((BodyData) fixB).regainHp(baseHeal, creator, true);
							SoundEffect.COIN3.playUniversal(state, hbox.getPixelPosition(), 0.5f, false);
							new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.REGEN, 1.0f, true, particleSyncType.CREATESYNC);
							hbox.die();
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
