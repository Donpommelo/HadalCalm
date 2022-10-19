package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
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
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class PartyPopper extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 22;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.6f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 61.0f;
	private static final float recoil = 12.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 120.0f;
	private static final Vector2 projectileSize = new Vector2(60, 60);
	private static final float lifespan = 0.3f;
	
	private static final int numProj = 8;
	private static final int spread = 30;
	private static final float fragSpeed = 50.0f;
	private static final Vector2 fragSize = new Vector2(18, 18);
	private static final float fragLifespan = 1.2f;
	private static final float fragDamage = 20.0f;
	private static final float fragKnockback = 2.0f;

	private static final float projDampen = 9.0f;
	private static final float fragDampen = 3.0f;
	
	private static final Sprite projSprite = Sprite.POPPER;
	private static final Sprite[] fragSprites = {Sprite.ORB_PINK, Sprite.ORB_RED, Sprite.ORB_BLUE, Sprite.ORB_YELLOW, Sprite.ORB_ORANGE};

	private static final Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private static final Sprite eventSprite = Sprite.P_BOOMERANG;
	
	public PartyPopper(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float[] fragAngles = new float[numProj];
		for (int i = 0; i < numProj; i++) {
			fragAngles[i] = new Vector2(0, 1).angleDeg() + MathUtils.random(-spread, spread + 1);
		}
		SyncedAttack.POPPER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, fragAngles);
	}

	public static Hitbox createPopper(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.CRACKER1.playSourced(state, startPosition, 1.0f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.PARTY_POPPER, DamageTag.RANGED));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.CRACKER2, 0.4f).setSynced(false));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.NOISEMAKER, 0.4f).setSynced(false));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.PARTY).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void create() {
				super.create();
				hbox.getBody().setLinearDamping(projDampen);
			}

			@Override
			public void die() {
				Vector2 newVelocity = new Vector2();
				for (float newDegrees : extraFields) {
					newVelocity.set(0, 1).nor().scl(fragSpeed);

					int randomIndex = MathUtils.random(fragSprites.length - 1);
					Sprite projSprite = fragSprites[randomIndex];

					Hitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), fragSize, fragLifespan,
							newVelocity.setAngleDeg(newDegrees), user.getHitboxfilter(), false, true, user, projSprite) {

						@Override
						public void create() {
							super.create();
							getBody().setLinearDamping(fragDampen);
						}
					};
					frag.setSyncDefault(false);
					frag.setGravity(7.5f);
					frag.setDurability(3);

					frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
					frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));
					frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), fragDamage, fragKnockback,
							DamageSource.PARTY_POPPER, DamageTag.RANGED));

					if (!state.isServer()) {
						((ClientState) state).addEntity(frag.getEntityID(), frag, false, ClientState.ObjectLayer.HBOX);
					}
				}
			}
		});

		return hbox;
	}

	@Override
	public float getBotRangeMax() { return 13.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) fragDamage),
				String.valueOf(numProj),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime)};
	}
}
