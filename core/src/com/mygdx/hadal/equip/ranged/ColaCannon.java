package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Cola;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.text.UIText;

public class ColaCannon extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 17;
	private static final float SHOOT_CD = 0.1f;
	private static final float RELOAD_TIME = 2.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 55.0f;
	private static final float MAX_CHARGE = 12000.0f;
	private static final float NOISE_THRESHOLD = 1800.0f;

	private static final float PROC_CD = .05f;
	private static final float FIRE_DURATION = 2.0f;
	private static final float VELO_DEPREC = 1.0f;
	private static final float MIN_VELO = 10.0f;
	private static final float MIN_DURATION = 0.5f;

	private static final Vector2 PROJECTILE_SIZE = Cola.PROJECTILE_SIZE;
	private static final float LIFESPAN = Cola.LIFESPAN;
	private static final float BASE_DAMAGE = Cola.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SLODGEGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SLODGEGUN;
	
	private final Vector2 lastMouse = new Vector2();
	private float lastNoise;

	public ColaCannon(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		//when released, spray weapon at mouse. Spray duration and velocity scale to charge
		if (processClip()) {
			SoundEffect.POPTAB.playUniversal(state, user.getPixelPosition(), 0.8f, false);

			final float duration = FIRE_DURATION * chargeCd / getChargeTime() + MIN_DURATION;
			final float velocity = PROJECTILE_SPEED * chargeCd / getChargeTime() + MIN_VELO;

			playerData.addStatus(new FiringWeapon(state, duration, playerData, playerData, velocity, MIN_VELO, VELO_DEPREC, PROJECTILE_SIZE.x, PROC_CD, this));

			charging = false;
			chargeCd = 0;
			lastNoise = 0;

			setReloadCd(RELOAD_TIME - duration);
		}
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.COLA.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public void update(PlayState state, float delta) {

		if (reloading || getClipLeft() == 0) { return; }

		charging = true;
		mouseLocation.set(user.getMouseHelper().getPixelPosition());

		//this prevents initial charge gain dependent on spawn location distance from (0, 0)
		if (lastMouse.isZero()) {
			lastMouse.set(mouseLocation);
		}

		//While held, gain charge equal to mouse movement from location last update
		if (chargeCd < getChargeTime()) {
			chargeCd += lastMouse.dst(mouseLocation);
			if (chargeCd >= getChargeTime()) {
				chargeCd = getChargeTime();
			}

			if (chargeCd > lastNoise + NOISE_THRESHOLD) {
				lastNoise += NOISE_THRESHOLD;
				SoundEffect.SHAKE.playUniversal(state, user.getPixelPosition(), 1.0f, false);
				new ParticleEntity(state, new Vector2(user.getPixelPosition()), Particle.COLA_IMPACT, 1.0f, true, SyncType.CREATESYNC);
			}
		}
		lastMouse.set(mouseLocation);
	}

	@Override
	public String getChargeText() {
		if (chargeCd < getChargeTime()) {
			return UIText.SHAKE_MOUSE.text();
		} else {
			return UIText.FIRE.text();
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(PROC_CD),
				String.valueOf(MIN_DURATION),
				String.valueOf(FIRE_DURATION),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE)};
	}
}
