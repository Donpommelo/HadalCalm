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
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.strategies.hitbox.*;

public class SlodgeNozzle extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 25;
	private static final float shootCd = 0.25f;
	private static final float reloadTime = 1.2f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 10.0f;
	private static final float recoil = 2.4f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeed = 25.0f;
	private static final Vector2 projectileSize = new Vector2(40, 40);
	private static final float lifespan = 1.5f;
	
	private static final float procCd = 0.05f;

	private static final float slowDura = 4.0f;
	private static final float slow = 0.6f;
	private static final float fireDuration = 0.8f;

	private static final Sprite weaponSprite = Sprite.MT_SLODGEGUN;
	private static final Sprite eventSprite = Sprite.P_SLODGEGUN;
	
	public SlodgeNozzle(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.SLODGE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createSlodge(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				false, true, user, Sprite.NOTHING);
		hbox.setGravity(3.0f);
		hbox.setDurability(3);
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.SLODGE_NOZZLE, DamageTag.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SLODGE, 0.0f, 1.0f)
				.setParticleSize(90).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SLODGE_STATUS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), slowDura, slow, Particle.SLODGE_STATUS));

		return hbox;
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		if (processClip()) {
			SoundEffect.DARKNESS1.playUniversal(state, user.getPixelPosition(), 0.9f, false);

			playerData.addStatus(new FiringWeapon(state, fireDuration, playerData, playerData, projectileSpeed, 0, 0, projectileSize.x, procCd, this));
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(fireDuration),
				String.valueOf(procCd),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
