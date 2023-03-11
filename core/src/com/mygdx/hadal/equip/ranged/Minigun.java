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
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.strategies.hitbox.*;

public class Minigun extends RangedWeapon {

	private static final int clipSize = 200;
	private static final int ammoSize = 800;
	private static final float shootCd = 0.05f;
	private static final float reloadTime = 2.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 20.0f;
	private static final float recoil = 0.25f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 50.0f;
	private static final Vector2 projectileSize = new Vector2(40, 10);
	private static final float lifespan = 1.5f;

	private static final float pitchSpread = 0.4f;
	private static final int spread = 8;

	private static final Sprite projSprite = Sprite.BULLET;
	private static final Sprite weaponSprite = Sprite.MT_MACHINEGUN;
	private static final Sprite eventSprite = Sprite.P_MACHINEGUN;
	
	private static final float maxCharge = 0.5f;
	private static final float selfSlowDura = 0.1f;
	private static final float selfSlowMag = 0.6f;
	
	private SoundEntity chargeSound, fireSound;
	private ParticleEntity slow;

	public Minigun(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
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
			chargeCd += (delta + shootCd);
		}
		
		playerData.addStatus(new Slodged(state, selfSlowDura, selfSlowMag, playerData, playerData, Particle.NOTHING));
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
	public void processEffects(PlayState state) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		boolean charging = shooting && user.getUiHelper().getChargePercent() < 1.0f;
		boolean firing = shooting && user.getUiHelper().getChargePercent() == 1.0f;

		if (!shooting && (chargeSound != null || (fireSound != null && fireSound.isOn()))) {
			SoundEffect.MINIGUN_DOWN.playSourced(state, user.getPixelPosition(), 0.5f);
		}

		if (shooting) {
			if (slow == null) {
				slow = new ParticleEntity(user.getState(), user, Particle.STUN, 0.0f, 0.0f, false, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(slow.getEntityID(), slow, false, PlayState.ObjectLayer.EFFECT);
				}
			}
			slow.turnOn();
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
				chargeSound = new SoundEntity(state, user, SoundEffect.MINIGUN_UP, 0.0f, 0.4f, 1.0f,
						true, true, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(chargeSound.getEntityID(), chargeSound, false, PlayState.ObjectLayer.EFFECT);
				}
			}
		} else {
			if (chargeSound != null) {
				chargeSound.terminate();
				chargeSound = null;
			}
		}

		if (firing) {
			if (fireSound == null) {
				fireSound = new SoundEntity(state, user, SoundEffect.MINIGUN_LOOP, 0.0f, 0.4f, 1.0f,
						true, true, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(fireSound.getEntityID(), fireSound, false, PlayState.ObjectLayer.EFFECT);
				}
			} else {
				fireSound.turnOn();
			}
		} else if (fireSound != null) {
			fireSound.turnOff();
		}
	}

	public static Hitbox createMinigunBullet(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				true, true, user, projSprite);
		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_BODY_HIT, 0.5f, true)
				.setPitchSpread(pitchSpread).setSynced(false));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_CONCRETE_HIT, 0.5f)
				.setPitchSpread(pitchSpread).setSynced(false));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.MINIGUN,
				DamageTag.BULLET, DamageTag.RANGED));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BULLET_TRAIL, 0.0f, 0.5f)
				.setRotate(true).setSyncType(SyncType.NOSYNC));

		return hbox;
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
				String.valueOf((int) baseDamage),
				String.valueOf(maxCharge),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
