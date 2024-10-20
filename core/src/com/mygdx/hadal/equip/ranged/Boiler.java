package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.BoilerFire;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class Boiler extends RangedWeapon {

	private static final int CLIP_SIZE = 90;
	private static final int AMMO_SIZE = 270;
	private static final float SHOOT_CD = 0.04f;
	private static final float RELOAD_TIME = 1.5f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 48.0f;

	private static final Vector2 PROJECTILE_SIZE = BoilerFire.PROJECTILE_SIZE;
	private static final float LIFESPAN = BoilerFire.LIFESPAN;
	private static final float BASE_DAMAGE = BoilerFire.BASE_DAMAGE;
	private static final float FIRE_DURATION = BoilerFire.FIRE_DURATION;
	private static final float FIRE_DAMAGE = BoilerFire.FIRE_DAMAGE;
	
	private static final Sprite WEAPON_SPRITE = Sprite.MT_BOILER;
	private static final Sprite EVENT_SPRITE = Sprite.P_BOILER;
	
	private SoundEntity fireSound;
	
	public Boiler(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BOILER_FIRE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public void unequip(PlayState state) {
		if (fireSound != null) {
			fireSound.terminate();
			fireSound = null;
		}
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getEquipHelper().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		if (shooting) {
			if (fireSound == null) {
				fireSound = new SoundEntity(state, user, SoundEffect.FLAMETHROWER, 0.0f, 0.8f, 1.0f, true,
						true, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(fireSound.getEntityID(), fireSound, false, ObjectLayer.EFFECT);
				}
			} else {
				fireSound.turnOn();
			}
		} else if (fireSound != null) {
			fireSound.turnOff();
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(FIRE_DURATION),
				String.valueOf((int) FIRE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
