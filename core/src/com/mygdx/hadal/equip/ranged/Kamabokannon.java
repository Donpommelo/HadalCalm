package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Kamaboko;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class Kamabokannon extends RangedWeapon {

	private static final int CLIP_SIZE = 100;
	private static final int AMMO_SIZE = 500;
	private static final float SHOOT_CD = 0.1f;
	private static final float RELOAD_TIME = 1.3f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 34.5f;
	private static final float MAX_CHARGE = 0.25f;
	private static final float LERP_SPEED = 0.3f;

	private static final Vector2 PROJECTILE_SIZE = Kamaboko.PROJECTILE_SIZE;
	private static final float LIFESPAN = Kamaboko.LIFESPAN;
	private static final float BASE_DAMAGE = Kamaboko.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_BOILER;
	private static final Sprite EVENT_SPRITE = Sprite.P_BOILER;

	private SoundEntity oozeSound;
	
	public Kamabokannon(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);
		mousePointer.set(weaponVelo);
		weaponVelo.set(aimPointer);

		if (reloading || getClipLeft() == 0) {
			return;
		}

		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			chargeCd += (delta + SHOOT_CD);
			
			if (chargeCd >= getChargeTime()) {
				super.mouseClicked(delta, state, playerData, faction, mouseLocation);
				
				aimPointer.set(weaponVelo);
			}
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		if (chargeCd >= getChargeTime()) {
			chargeCd = getChargeTime();
			super.execute(state, playerData);
		}
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.KAMABOKO.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public void processEffects(PlayState state, float delta) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool())
				&& !reloading && getClipLeft() > 0 && user.getUiHelper().getChargePercent() == 1.0f;

		if (shooting) {
			if (oozeSound == null) {
				oozeSound = new SoundEntity(state, user, SoundEffect.OOZE, 0.0f, 0.8f, 1.0f,
						true, true, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(oozeSound.getEntityID(), oozeSound, false, PlayState.ObjectLayer.EFFECT);
				}
			} else {
				oozeSound.turnOn();
			}
		} else if (oozeSound != null) {
			oozeSound.turnOff();
		}
	}

	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		chargeCd = 0;
		charging = false;
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
			aimPointer.setAngleDeg(MathUtils.lerpAngleDeg(aimPointer.angleDeg(), mousePointer.angleDeg(), LERP_SPEED));
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(MAX_CHARGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
