package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Peppergrinder extends RangedWeapon {

	private static final int clipSize = 60;
	private static final int ammoSize = 360;
	private static final float shootCd = 0.06f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 19.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 10.0f;
	private static final float projectileSpeed = 31.5f;
	private static final Vector2 projectileSize = new Vector2(40, 20);
	private static final float lifespan = 2.0f;

	private static final Sprite projSprite = Sprite.LASER_GREEN;
	private static final Sprite weaponSprite = Sprite.MT_BOILER;
	private static final Sprite eventSprite = Sprite.P_BOILER;

	private static final int maxSpread = 24;
	private static final int spreadChange = 8;

	public Peppergrinder(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	private int spread;
	private boolean sweepingUp;

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.PEPPER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, spread);

		if (sweepingUp) {
			spread += spreadChange;
			if (spread >= maxSpread) {
				sweepingUp = false;
			}
		} else {
			spread -= spreadChange;
			if (spread <= -maxSpread) {
				sweepingUp = true;
			}
		}
	}

	public static Hitbox createPepper(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.LASER2.playSourced(state, user.getPixelPosition(), 0.25f);
		user.recoil(startVelocity, recoil);

		float spread = 0.0f;
		if (extraFields.length > 0) {
			spread = extraFields[0];
		}
		float finalSpread = spread;

		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.PALE_GREEN).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.PALE_GREEN).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.25f, true).setSynced(false));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void create() {
				float newDegrees = hbox.getStartVelo().angleDeg() + finalSpread;
				hbox.setLinearVelocity(hbox.getLinearVelocity().setAngleDeg(newDegrees));
			}
		});

		return hbox;
	}
}
