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
import com.mygdx.hadal.states.ClientState;
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
	private static final float NOISE_THRESHOLD = 0.15f;

	private static final float PROC_CD = .05f;
	private static final float FIRE_DURATION = 2.0f;
	private static final int SHOT_NUMBER = 50;
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

	private float lastVelocity;
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		//when released, spray weapon at mouse. Spray duration and velocity scale to charge
		if (processClip()) {

			float duration = FIRE_DURATION * chargeCd / getChargeTime() + MIN_DURATION;
			lastVelocity = PROJECTILE_SPEED * chargeCd / getChargeTime() + MIN_VELO;

			playerData.addStatus(new FiringWeapon(state, duration, playerData, playerData, PROJECTILE_SIZE.x, PROC_CD, SHOT_NUMBER, this));

			charging = false;
			chargeCd = 0;

			setReloadCd(RELOAD_TIME - duration);
		}
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		int shotNum = user.getSpecialWeaponHelper().getSprayWeaponShotNumber();
		float velocity = Math.max(MIN_VELO, lastVelocity - VELO_DEPREC * shotNum);
		SyncedAttack.COLA.initiateSyncedAttackSingle(state, user, startPosition, startVelocity.nor().scl(velocity),	shotNum);
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
		}
		lastMouse.set(mouseLocation);
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean charging = this.equals(user.getPlayerData().getCurrentTool()) && !reloading && getClipLeft() > 0;

		if (charging && user.getUiHelper().getChargePercent() > lastNoise + NOISE_THRESHOLD) {

			if (lastNoise != 0.0f || user.getUiHelper().getChargePercent() + NOISE_THRESHOLD < 1.0f) {
				lastNoise += NOISE_THRESHOLD;
				if (lastNoise + NOISE_THRESHOLD >= 1.0f) {
					lastNoise = 0.0f;
				}

				SoundEffect.SHAKE.playSourced(state, playerPosition, 1.0f);
				ParticleEntity particle = new ParticleEntity(state, new Vector2(playerPosition), Particle.COLA_IMPACT, 1.0f,
						true, SyncType.NOSYNC);

				if (!state.isServer()) {
					((ClientState) state).addEntity(particle.getEntityID(), particle, false, PlayState.ObjectLayer.EFFECT);
				}
			}
		}
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
