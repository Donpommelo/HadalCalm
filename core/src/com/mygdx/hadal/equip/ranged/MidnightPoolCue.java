package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.PoolBall;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class MidnightPoolCue extends RangedWeapon {

	private static final int CLIP_SIZE = 16;
	private static final int AMMO_SIZE = 80;
	private static final float SHOOT_CD = 0.0f;
	private static final float RELOAD_TIME = 1.9f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 10.0f;
	private static final float MAX_CHARGE = 0.4f;

	//keeps track of attack speed without input buffer doing an extra mouse click
	private static final float INNATE_ATTACK_COOLDOWN = 0.6f;

	private static final Vector2 PROJECTILE_SIZE = PoolBall.PROJECTILE_SIZE;
	private static final float LIFESPAN = PoolBall.LIFESPAN;
	private static final float BASE_DAMAGE = PoolBall.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SPEARGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SPEARGUN;

	private float innateAttackCdCount;

	private SoundEntity chargeSound;

	public MidnightPoolCue(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, playerData, faction, mousePosition);

		if (innateAttackCdCount <= 0.0f) {
			if (reloading || getClipLeft() == 0) {
				return;
			}

			charging = true;

			//while held, build charge until maximum (if not reloading)
			if (chargeCd < getChargeTime()) {
				setChargeCd(chargeCd + delta);
			}
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {}
	
	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		if (innateAttackCdCount <= 0.0f) {
			super.execute(state, playerData);
		}
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float charge = chargeCd / getChargeTime();
		SyncedAttack.POOL_BALL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, charge);

		innateAttackCdCount = INNATE_ATTACK_COOLDOWN * (1 - user.getBodyData().getStat(Stats.TOOL_SPD));
	}

	@Override
	public void update(PlayState state, float delta) {
		if (innateAttackCdCount > 0) {
			innateAttackCdCount -= delta;
		}
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool())
				&& !reloading && getClipLeft() > 0 && user.getUiHelper().getChargePercent() < 1.0f;

		if (shooting) {
			if (chargeSound == null) {
				chargeSound = new SoundEntity(state, user, SoundEffect.BOW_STRETCH, 0.0f, 1.0f, 1.0f,
						true, true, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(chargeSound.getEntityID(), chargeSound, false, PlayState.ObjectLayer.EFFECT);
				}
			}
		} else if (chargeSound != null) {
			chargeSound.terminate();
			chargeSound = null;
		}
	}

	@Override
	public void unequip(PlayState state) {
		if (chargeSound != null) {
			chargeSound.terminate();
			chargeSound = null;
		}
	}
	@Override
	public float getBotRangeMax() { return 40.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE * PoolBall.MIN_DAMAGE_MULTIPLIER),
				String.valueOf((int) BASE_DAMAGE * PoolBall.MAX_DAMAGE_MULTIPLIER),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(MAX_CHARGE)};
	}
}
