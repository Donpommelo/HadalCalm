package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.strategies.hitbox.*;

public class StutterGun extends RangedWeapon {

	private static final int clipSize = 5;
	private static final int ammoSize = 35;
	private static final float shootCd = 0.6f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 18.0f;
	private static final float recoil = 3.5f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(80, 40);
	private static final float lifespan = 1.0f;
	
	private static final float procCd = 0.09f;
	private static final float fireDuration = 0.5f;
	
	private static final float pitchSpread = 0.4f;
	private static final int spread = 8;

	private static final Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private static final Sprite eventSprite = Sprite.P_LASERRIFLE;
	
	public StutterGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.STUTTER_LASER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createStutterLaser(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		float pitch = (MathUtils.random() - 0.5f) * pitchSpread;
		SoundEffect.LASER2.playSourced(state, user.getPixelPosition(), 0.5f, 1.0f + pitch);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.LASER_ORANGE);
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.ORANGE).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.ORANGE).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true).setSynced(false));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.STUTTERGUN, DamageTag.ENERGY, DamageTag.RANGED));

		return hbox;
	}

	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (processClip()) {
			shooter.addStatus(new FiringWeapon(state, fireDuration, shooter, shooter, projectileSpeed, 0, 0, projectileSize.x, procCd, this));
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) (fireDuration / procCd)),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
