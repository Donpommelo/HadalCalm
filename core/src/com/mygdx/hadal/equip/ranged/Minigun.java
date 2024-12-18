package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.MinigunBullet;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Slodged;

public class Minigun extends RangedWeapon {

	private static final int CLIP_SIZE = 200;
	private static final int AMMO_SIZE = 800;
	private static final float SHOOT_CD = 0.05f;
	private static final float RELOAD_TIME = 2.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 50.0f;
	private static final float MAX_CHARGE = 0.5f;
	private static final float SELF_SLOW_DURA = 0.1f;
	private static final float SELF_SLOW_MAG = 0.6f;

	private static final Vector2 PROJECTILE_SIZE = MinigunBullet.PROJECTILE_SIZE;
	private static final float LIFESPAN = MinigunBullet.LIFESPAN;
	private static final float BASE_DAMAGE = MinigunBullet.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_MACHINEGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_MACHINEGUN;

	private SoundEntity chargeSound, fireSound;
	private ParticleEntity slow;

	public Minigun(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);

		if (reloading || getClipLeft() == 0) {
			return;
		}

		charging = true;
		
		//while held, build charge until maximum (if not reloading) User is slowed while shooting.
		if (chargeCd < getChargeTime()) {
			chargeCd += (delta + SHOOT_CD);
		}
		
		playerData.addStatus(new Slodged(state, SELF_SLOW_DURA, SELF_SLOW_MAG, playerData, playerData, Particle.NOTHING));
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		if (chargeCd >= getChargeTime()) {
			chargeCd = getChargeTime();
			super.execute(state, playerData);
		}
	}
	
	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.MINIGUN_BULLET.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getEquipHelper().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		boolean charging = shooting && user.getUiHelper().getChargePercent() < 1.0f;
		boolean firing = shooting && user.getUiHelper().getChargePercent() == 1.0f;

		if (!shooting && (chargeSound != null || (fireSound != null && fireSound.isOn()))) {
			SoundManager.play(state, new SoundLoad(SoundEffect.MINIGUN_DOWN)
					.setVolume(0.5f)
					.setPosition(playerPosition));
		}

		if (shooting) {
			if (slow == null) {
				slow = EffectEntityManager.getParticle(state, new ParticleCreate(Particle.STUN, user)
						.setStartOn(false)
						.setScale(0.6f));
			}
			if (slow != null) {
				slow.turnOn();
			}
		} else if (slow != null) {
			if (state.isServer()) {
				slow.queueDeletion();
			} else {
				((ClientState) state).removeEntity(slow.getEntityID());
			}
			slow = null;
		}

		if (charging) {
			if (chargeSound == null) {
				chargeSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.MINIGUN_UP, user)
						.setVolume(0.8f));
			}
		} else {
			if (chargeSound != null) {
				chargeSound.terminate();
				chargeSound = null;
			}
		}

		if (firing) {
			if (fireSound == null) {
				fireSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.MINIGUN_LOOP, user)
						.setVolume(0.4f));
			} else {
				fireSound.turnOn();
			}
		} else if (fireSound != null) {
			fireSound.turnOff();
		}
	}

	@Override
	public void unequip(PlayState state) {
		if (chargeSound != null) {
			chargeSound.terminate();
			chargeSound = null;
		}
		if (fireSound != null) {
			fireSound.terminate();
			fireSound = null;
		}
		if (slow != null) {
			if (state.isServer()) {
				slow.queueDeletion();
			} else {
				((ClientState) state).removeEntity(slow.getEntityID());
			}
			slow = null;
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
