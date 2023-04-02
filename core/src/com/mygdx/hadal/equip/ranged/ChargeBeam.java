package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class ChargeBeam extends RangedWeapon {

	private static final int clipSize = 4;
	private static final int ammoSize = 16;
	private static final float shootCd = 0.0f;
	private static final float reloadTime = 1.3f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 18.0f;
	private static final float recoil = 7.5f;
	private static final float knockback = 10.0f;
	private static final float projectileSpeed = 60.0f;
	private static final Vector2 projectileSize = new Vector2(35, 35);
	private static final float lifespan = 0.6f;

	private static final Sprite projSprite = Sprite.CHARGE_BEAM;
	private static final Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private static final Sprite eventSprite = Sprite.P_CHARGEBEAM;

	private static final float maxCharge = 0.5f;
	private static final float maxDamageMultiplier = 5.0f;
	private static final float particleOffset = -1.85f;
	private ParticleEntity charge, overcharge;

	public ChargeBeam(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x * 3.0f, lifespan, maxCharge);
	}

	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);

		if (reloading || getClipLeft() == 0) {
			return;
		}
		charging = true;

		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
		}
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {}

	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		super.execute(state, playerData);
		charging = false;
		chargeCd = 0;
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float charge = chargeCd / getChargeTime();
		SyncedAttack.CHARGE_BEAM.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, charge);
	}

	@Override
	public void unequip(PlayState state) {
		if (charge != null) {
			charge.queueDeletion();
			charge = null;
		}
		if (overcharge != null) {
			overcharge.queueDeletion();
			overcharge = null;
		}
	}

	private final Vector2 particleOrigin = new Vector2(0, 1);
	@Override
	public void processEffects(PlayState state, float delta) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		boolean charging = shooting && user.getUiHelper().getChargePercent() < 1.0f;
		boolean overcharging = shooting && user.getUiHelper().getChargePercent() == 1.0f;

		particleOrigin.setAngleDeg(user.getMouseHelper().getAttackAngle()).nor().scl(user.getSize().x * particleOffset);

		if (charging) {
			if (charge == null) {
				charge = new ParticleEntity(user.getState(), user, Particle.CHARGING, 1.0f, 0.0f, false, SyncType.NOSYNC);
				charge.setScale(0.6f);

				if (!state.isServer()) {
					((ClientState) state).addEntity(charge.getEntityID(), charge, false, PlayState.ObjectLayer.EFFECT);
				}
			}
			charge.setOffset(particleOrigin.x, particleOrigin.y);
			charge.turnOn();
		} else if (charge != null) {
			charge.turnOff();
		}

		if (overcharging) {
			if (overcharge == null) {
				overcharge = new ParticleEntity(user.getState(), user, Particle.OVERCHARGE, 1.0f, 0.0f, false, SyncType.NOSYNC);
				overcharge.setScale(0.6f);

				if (!state.isServer()) {
					((ClientState) state).addEntity(overcharge.getEntityID(), overcharge, false, PlayState.ObjectLayer.EFFECT);
				}
			}
			overcharge.setOffset(particleOrigin.x, particleOrigin.y);
			overcharge.turnOn();
		} else if (overcharge != null) {
			overcharge.turnOff();
		}
	}

	public static Hitbox createChargeBeam(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.LASERHARPOON.playSourced(state, startPosition, 0.8f);
		user.recoil(startVelocity, recoil);

		float chargeAmount = 0.0f;
		if (extraFields.length > 0) {
			chargeAmount = extraFields[0];
		}
		int chargeStage;

		//power of hitbox scales to the amount charged
		if (chargeAmount >= 1.0f) {
			chargeStage = 2;
		} else if (chargeAmount >= 0.5f) {
			chargeStage = 1;
		} else {
			chargeStage = 0;
		}

		float sizeMultiplier = 1.0f;
		float damageMultiplier = 1.5f;
		float kbMultiplier = 1;

		switch (chargeStage) {
			case 2 -> {
				sizeMultiplier = 2.0f;
				damageMultiplier = maxDamageMultiplier;
				kbMultiplier = 3.0f;
			}
			case 1 -> {
				sizeMultiplier = 1.2f;
				damageMultiplier = 2.5f;
				kbMultiplier = 2.0f;
			}
		}

		final float damageMultiplier2 = damageMultiplier;
		final float kbMultiplier2 = kbMultiplier;

		Hitbox wallCollider = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				true, true, user, Sprite.NOTHING);
		wallCollider.setEffectsHit(false);
		wallCollider.setEffectsMovement(false);
		wallCollider.setEffectsVisual(false);

		wallCollider.addStrategy(new ControllerDefault(state, wallCollider, user.getBodyData()));
		wallCollider.addStrategy(new ContactWallDie(state, wallCollider, user.getBodyData()));

		Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(projectileSize).scl(sizeMultiplier), lifespan, startVelocity,
				user.getHitboxFilter(), true, true, user, projSprite);
		hbox.setSyncDefault(false);
		hbox.setDurability(3);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.6f, true).setSynced(false));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), wallCollider, new Vector2(), new Vector2()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					fixB.receiveDamage(baseDamage * damageMultiplier2, this.hbox.getLinearVelocity().nor().scl(knockback * kbMultiplier2),
							user.getBodyData(), true, hbox, DamageSource.CHARGE_BEAM, DamageTag.ENERGY, DamageTag.RANGED);
				}
			}
		});

		if (chargeStage == 2) {
			hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.OVERCHARGE, 0.0f, 1.0f)
					.setParticleSize(70).setSyncType(SyncType.NOSYNC));
			hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.OVERCHARGE).setParticleDuration(0.4f)
					.setSyncType(SyncType.NOSYNC));
		}

		if (!state.isServer()) {
			((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
		}
		return wallCollider;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) (baseDamage * maxDamageMultiplier)),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(maxCharge)};
	}
}
