package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Moraygun extends RangedWeapon {

	private static final int clipSize = 7;
	private static final int ammoSize = 42;
	private static final float shootCd = 0.3f;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 13.0f;
	private static final float recoil = 12.0f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeedStart = 150.0f;
	private static final Vector2 projectileSize = new Vector2(30, 30);
	private static final float lifespan = 2.0f;
	
	private static final Sprite projSprite = Sprite.ORB_PINK;
	private static final Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private static final Sprite eventSprite = Sprite.P_CHARGEBEAM;

	private static final int numProj = 6;
	private static final float moveInterval = 0.023f;
	
	public Moraygun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeedStart, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Vector2[] positions = new Vector2[numProj];
		Vector2[] velocities = new Vector2[numProj];
		for (int i = 0; i < numProj; i++) {
			positions[i] = startPosition;
			velocities[i] = startVelocity;
		}
		final int numX = (int) (startVelocity.x / projectileSize.x);
		final int numY = (int) (startVelocity.y / projectileSize.y);
		SyncedAttack.MORAY.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities, numX, numY);
	}

	public static Hitbox[] createMoray(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition, float[] extraFields) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];
		if (startPosition.length != 0) {
			SoundEffect.LASERSHOT.playSourced(state, startPosition[0], 0.9f);
			user.recoil(weaponVelocity, recoil);

			final int numX = extraFields.length >= 2 ? (int) extraFields[0] : 0;
			final int numY = extraFields.length >= 2 ? (int) extraFields[1] : 0;

			//create a set number of hboxes that die when hitting enemies or walls.
			for (int i = 0; i < startPosition.length; i++) {
				Hitbox hbox = new RangedHitbox(state, startPosition[i], projectileSize, lifespan, new Vector2(), user.getHitboxFilter(),
						true, true, user, projSprite);
				hbox.setSyncDefault(false);

				final int num = i;

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.MORAYGUN,
						DamageTag.ENERGY, DamageTag.RANGED).setStaticKnockback(true));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true).setSynced(false));
				hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ORB_SWIRL).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

					private float controllerCount;
					private float numMoves;
					@Override
					public void controller(float delta) {
						controllerCount += delta;

						//Each hbox moves at set intervals. Each movement moves the hbox vertical x times followed by horizontal y times to make a snake-like movement
						while (controllerCount >= moveInterval) {
							controllerCount -= moveInterval;

							if (numMoves >= num) {
								if ((numMoves - num) % (Math.abs(numX) + Math.abs(numY)) < Math.abs(numX)) {
									hbox.setTransform(hbox.getPosition().add(projectileSize.x / PPM * Math.signum(numX), 0), 0);
								} else {
									hbox.setTransform(hbox.getPosition().add(0, projectileSize.y / PPM * Math.signum(numY)), 0);
								}
							}
							numMoves++;
						}
					}
				});
				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}

	@Override
	public float getBotRangeMax() { return 80.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(numProj),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
