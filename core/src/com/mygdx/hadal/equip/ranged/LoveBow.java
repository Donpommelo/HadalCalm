package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.LoveArrow;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class LoveBow extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 50;
	private static final float SHOOT_CD = 0.0f;
	private static final float RELOAD_TIME = 0.6f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 15.0f;
	private static final float maxCharge = 0.25f;

	private static final Vector2 PROJECTILE_SIZE = LoveArrow.PROJECTILE_SIZE;
	private static final float LIFESPAN = LoveArrow.LIFESPAN;
	private static final float MIN_DAMAGE = LoveArrow.MIN_DAMAGE;
	private static final float MAX_DAMAGE = LoveArrow.MAX_DAMAGE;
	private static final float MIN_HEAL = LoveArrow.MIN_HEAL;
	private static final float MAX_HEAL = LoveArrow.MAX_HEAL;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SPEARGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SPEARGUN;

	private SoundEntity chargeSound;

	public LoveBow(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, playerData, faction, mousePosition);

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
		SyncedAttack.LOVE_ARROW.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, charge);
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
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MIN_DAMAGE),
				String.valueOf((int) MAX_DAMAGE),
				String.valueOf((int) MIN_HEAL),
				String.valueOf((int) MAX_HEAL),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(maxCharge)};
	}
}
