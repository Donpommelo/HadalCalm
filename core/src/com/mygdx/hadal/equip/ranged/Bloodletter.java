package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.BloodletterProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.PlayState;

public class Bloodletter extends RangedWeapon {

	private static final int CLIP_SIZE = 24;
	private static final int AMMO_SIZE = 125;
	private static final float SHOOT_CD = 0.6f;
	private static final float RELOAD_TIME = 1.6f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 1.0f;

	private static final Vector2 PROJECTILE_SIZE = BloodletterProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = BloodletterProjectile.LIFESPAN;
	private static final float BASE_DAMAGE = BloodletterProjectile.BASE_DAMAGE;
	public static final float HEAL_MULTIPLIER = BloodletterProjectile.HEAL_MULTIPLIER;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_BOILER;
	private static final Sprite EVENT_SPRITE = Sprite.P_BOILER;

	private SoundEntity fireSound;

	public Bloodletter(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BLOODLETTER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
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
				fireSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.STRAW, user)
						.setVolume(1.2f)
						.setPitch(0.7f));
			} else {
				fireSound.turnOn();
			}
		} else {
			if (fireSound != null) {
				fireSound.turnOff();
			}
		}
	}

	@Override
	public float getBotRangeMax() {
		return 11.5f;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) (HEAL_MULTIPLIER * 100)),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
