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
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Boiler extends RangedWeapon {

	private static final int clipSize = 90;
	private static final int ammoSize = 270;
	private static final float shootCd = 0.04f;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 6.0f;
	private static final float recoil = 1.5f;
	private static final float knockback = 2.0f;
	private static final float projectileSpeed = 48.0f;
	private static final Vector2 projectileSize = new Vector2(100, 50);
	private static final float lifespan = 0.35f;
	
	private static final float fireDuration = 5.0f;
	private static final float fireDamage = 3.0f;
	
	private static final Sprite weaponSprite = Sprite.MT_BOILER;
	private static final Sprite eventSprite = Sprite.P_BOILER;
	
	private SoundEntity fireSound;
	
	public Boiler(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
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
	public void processEffects(PlayState state) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		if (shooting) {
			if (fireSound == null) {
				fireSound = new SoundEntity(state, user, SoundEffect.FLAMETHROWER, 0.0f, 0.8f, 1.0f, true,
						true, SyncType.NOSYNC);
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

	public static Hitbox createBoilerFire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		user.recoil(startVelocity, recoil);

		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				false, true, user, Sprite.NOTHING);
		hbox.setDurability(3);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitBurn(state, hbox, user.getBodyData(), fireDuration, fireDamage, DamageSource.BOILER));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.BOILER,
				DamageTag.FIRE, DamageTag.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 0.0f, 1.0f)
				.setSyncType(SyncType.NOSYNC));
		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(fireDuration),
				String.valueOf((int) fireDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
