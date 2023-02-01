package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class CR4PCannon extends RangedWeapon {

	private static final int clipSize = 2;
	private static final int ammoSize = 22;
	private static final float shootCd = 0.15f;
	private static final float reloadTime = 1.2f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 13.0f;
	private static final float recoil = 11.0f;
	private static final float knockback = 2.2f;
	private static final float projectileSpeed = 35.0f;
	private static final Vector2 projectileSize = new Vector2(20, 20);
	private static final float lifespan = 0.7f;

	private static final float pitchSpread = 0.4f;

	private static final int numProj = 9;
	private static final int spread = 15;
	
	private static final Sprite[] projSprites = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};
	private static final Sprite weaponSprite = Sprite.MT_SHOTGUN;
	private static final Sprite eventSprite = Sprite.P_SHOTGUN;
	
	public CR4PCannon(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Vector2[] positions = new Vector2[numProj];
		Vector2[] velocities = new Vector2[numProj];
		for (int i = 0; i < numProj; i++) {
			positions[i] = startPosition;
			velocities[i] = startVelocity;
		}
		SyncedAttack.CR4P.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
	}

	public static Hitbox[] createCR4P(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition, Vector2[] startVelocity) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];
		if (startPosition.length != 0) {
			SoundEffect.SHOTGUN.playSourced(state, startPosition[0], 0.75f);
			user.recoil(weaponVelocity, recoil);

			for (int i = 0; i < startPosition.length; i++) {

				int randomIndex = MathUtils.random(projSprites.length - 1);
				Sprite projSprite = projSprites[randomIndex];

				Hitbox hbox = new RangedHitbox(state, startPosition[i], projectileSize, lifespan, startVelocity[i], user.getHitboxFilter(),
						true, true, user, projSprite);
				hbox.setGravity(0.5f);
				hbox.setDurability(2);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_BODY_HIT, 0.3f, true)
						.setPitchSpread(pitchSpread).setSynced(false));
				hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_CONCRETE_HIT, 0.3f)
						.setPitchSpread(pitchSpread).setSynced(false));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.CR4P_CANNON,
						DamageTag.SHRAPNEL, DamageTag.RANGED));
				hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}

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
