package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.ChargeBeamProjectile;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class ChargeBeam extends RangedWeapon {

	private static final int CLIP_SIZE = 4;
	private static final int AMMO_SIZE = 16;
	private static final float SHOOT_CD = 0.0f;
	private static final float RELOAD_TIME = 1.3f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 60.0f;
	private static final float MAX_CHARGE = 0.5f;

	private static final Vector2 PROJECTILE_SIZE = ChargeBeamProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = ChargeBeamProjectile.LIFESPAN;
	private static final float BASE_DAMAGE = ChargeBeamProjectile.BASE_DAMAGE;
	private static final float MAX_DAMAGE_MULTIPLIER = ChargeBeamProjectile.MAX_DAMAGE_MULTIPLIER;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_CHARGEBEAM;
	private static final Sprite EVENT_SPRITE = Sprite.P_CHARGEBEAM;
	private static final float PARTICLE_OFFSET = -1.85f;

	private ParticleEntity charge, overcharge;

	public ChargeBeam(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x * 3.0f, LIFESPAN, MAX_CHARGE);
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
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		boolean charging = shooting && user.getUiHelper().getChargePercent() < 1.0f;
		boolean overcharging = shooting && user.getUiHelper().getChargePercent() == 1.0f;

		particleOrigin.setAngleDeg(user.getMouseHelper().getAttackAngle()).nor().scl(user.getSize().x * PARTICLE_OFFSET);

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

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) (BASE_DAMAGE * MAX_DAMAGE_MULTIPLIER)),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(MAX_CHARGE)};
	}
}
