package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.WaveEntity;

public class WaveBeam extends RangedWeapon {

	private static final int clipSize = 5;
	private static final int ammoSize = 25;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 35.0f;
	private static final float recoil = 12.5f;
	private static final float knockback = 28.0f;
	private static final float projectileSpeed = 42.0f;
	private static final Vector2 projectileSize = new Vector2(60, 30);
	private static final float lifespan = 1.5f;

	private static final Sprite projSprite = Sprite.LASER_BLUE;
	private static final Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private static final Sprite eventSprite = Sprite.P_LASERRIFLE;

	private static final float amplitude = 1.0f;
	private static final float frequency = 25.0f;

	public WaveBeam(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.WAVE_BEAM.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createWaveBeam(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.SHOOT1.playSourced(state, startPosition, 0.6f);
		user.recoil(startVelocity, recoil);

		//we create an invisible hitbox that moves in a straight line.
		Hitbox center = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.NOTHING);
		center.setEffectsHit(false);
		center.setEffectsVisual(false);

		center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
		center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));
		center.addStrategy(new HitboxStrategy(state, center, user.getBodyData()) {

			@Override
			public void create() {
				createWaveBeam(90);
				createWaveBeam(-90);
			}

			private void createWaveBeam(float startAngle) {
				Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
						true, true, user, projSprite);
				hbox.setSyncDefault(false);
				hbox.setEffectsMovement(false);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
						.setParticleColor(HadalColor.BLUE).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
						.setParticleColor(HadalColor.BLUE).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.4f, true).setSynced(false));
				hbox.addStrategy(new WaveEntity(state, hbox, user.getBodyData(), center, amplitude, frequency, startAngle));

				if (!state.isServer()) {
					((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
				}
			}
		});

		return center;
	}
}