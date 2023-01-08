package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.constants.Constants;

public class ReticleStrike extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 25;
	private static final float shootCd = 0.35f;
	private static final float reloadTime = 0.9f;
	private static final int reloadAmount = 1;
	private static final float recoil = 16.0f;
	private static final float projectileSpeed = 80.0f;
	private static final Vector2 projectileSize = new Vector2(10, 10);
	private static final float lifespan = 1.2f;

	private static final Sprite projSprite = Sprite.NOTHING;
	private static final Sprite weaponSprite = Sprite.MT_IRONBALL;
	private static final Sprite eventSprite = Sprite.P_IRONBALL;

	private static final float reticleSize = 80.0f;
	private static final float reticleSpacing = 110.0f;
	private static final float reticleSizeSquared = 12100;
	private static final float reticleLifespan = 0.6f;
	private static final int explosionRadius = 100;
	private static final float explosionDamage = 35.0f;
	private static final float explosionKnockback = 20.0f;
	
	public ReticleStrike(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.RETICLE_STRIKE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createReticleStrike(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.LOCKANDLOAD.playSourced(state, startPosition, 0.8f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private final Vector2 lastPosition = new Vector2(startPosition);
			@Override
			public void controller(float delta) {
				if (lastPosition.dst2(hbox.getPixelPosition()) > reticleSizeSquared) {
					lastPosition.add(new Vector2(hbox.getPixelPosition()).sub(lastPosition).nor().scl(reticleSpacing));

					Hitbox reticle = new RangedHitbox(state, lastPosition, new Vector2(reticleSize, reticleSize), reticleLifespan,
							new Vector2(), user.getHitboxfilter(), true, true, user, Sprite.CROSSHAIR);
					reticle.setSyncDefault(false);
					reticle.setEffectsMovement(false);
					reticle.setEffectsHit(false);
					reticle.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

					reticle.addStrategy(new ControllerDefault(state, reticle, user.getBodyData()));
					reticle.addStrategy(new CreateParticles(state, reticle, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f)
							.setParticleSize(40.0f).setParticleColor(HadalColor.HOT_PINK).setSyncType(SyncType.NOSYNC));
					reticle.addStrategy(new DieExplode(state, reticle, user.getBodyData(), explosionRadius, explosionDamage,
							explosionKnockback, user.getHitboxfilter(), false, DamageSource.RETICLE_STRIKE));
					reticle.addStrategy(new DieSound(state, reticle, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f).setSynced(false));
					reticle.addStrategy(new Static(state, reticle, user.getBodyData()));

					if (!state.isServer()) {
						((ClientState) state).addEntity(reticle.getEntityID(), reticle, false, ClientState.ObjectLayer.HBOX);
					}
				}
			}
		});

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) explosionDamage),
				String.valueOf(reticleLifespan),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime)};
	}
}
