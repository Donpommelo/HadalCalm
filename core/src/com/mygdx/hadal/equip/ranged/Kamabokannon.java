package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class Kamabokannon extends RangedWeapon {

	private static final int clipSize = 100;
	private static final int ammoSize = 500;
	private static final float shootCd = 0.1f;
	private static final float reloadTime = 1.3f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 22.0f;
	private static final float recoil = 3.0f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 34.5f;
	private static final Vector2 projectileSize = new Vector2(60, 50);
	private static final float lifespan = 1.0f;
	
	private static final Sprite weaponSprite = Sprite.MT_BOILER;
	private static final Sprite eventSprite = Sprite.P_BOILER;
	
	private static final float maxCharge = 0.25f;
	private static final float lerpSpeed = 0.3f;

	private SoundEntity oozeSound;
	
	public Kamabokannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		mousePointer.set(weaponVelo);
		weaponVelo.set(aimPointer);

		if (reloading || getClipLeft() == 0) {
			if (oozeSound != null) {
				oozeSound.turnOff();
			}
			return;
		}

		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			chargeCd += (delta + shootCd);
			
			if (chargeCd >= getChargeTime()) {
				super.mouseClicked(delta, state, shooter, faction, mouseLocation);
				
				aimPointer.set(weaponVelo);
				
				if (oozeSound == null) {
					oozeSound = new SoundEntity(state, user, SoundEffect.OOZE, 0.0f, 0.8f, 1.0f,
							true, true, SyncType.TICKSYNC);
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
		SyncedAttack.KAMABOKO.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createKamaboko(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		user.recoil(startVelocity, recoil);

		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				true, true, user, Sprite.NOTHING);
		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.KAMABOKANNON,
				DamageTag.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 1.0f)
				.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_IMPACT).setSyncType(SyncType.NOSYNC));

		return hbox;
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

	private float controllerCount;
	private static final float pushInterval = 1 / 60f;
	private final Vector2 mousePointer = new Vector2();
	private final Vector2 aimPointer = new Vector2();
	@Override
	public void update(PlayState state, float delta) {
		controllerCount += delta;

		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			aimPointer.setAngleDeg(MathUtils.lerpAngleDeg(aimPointer.angleDeg(), mousePointer.angleDeg(), lerpSpeed));
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(maxCharge),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
