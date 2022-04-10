package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Iceberg extends RangedWeapon {

	private static final int clipSize = 4;
	private static final int ammoSize = 25;
	private static final float shootCd = 0.75f;
	private static final float shootDelay = 0.15f;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 45.0f;
	private static final float recoil = 15.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(48, 48);
	private static final float lifespan = 3.0f;

	private static final Sprite projSprite = Sprite.ICEBERG;
	private static final Sprite weaponSprite = Sprite.MT_ICEBERG;
	private static final Sprite eventSprite = Sprite.P_ICEBERG;

	public Iceberg(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.ICEBERG.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createIceberg(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.ICE_IMPACT.playSourced(state, startPosition, 0.9f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);
		hbox.setGravity(5);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.ICEBERG,
				DamageTag.WHACKING, DamageTag.RANGED).setRepeatable(true));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD, 0.0f, 2.0f).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ICE_IMPACT).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.ICE_IMPACT).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.CHILL_HIT, 0.6f, true).setSynced(false));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			float lastX;
			@Override
			public void controller(float delta) {

				//when we hit a wall, we reverse momentum instead of staying still.
				//This is necessary b/c we cannot turn restitution up without having the projectile bounce instead of slide,
				if (hbox.getLinearVelocity().x == 0) {
					hbox.setLinearVelocity(-lastX, hbox.getLinearVelocity().y);
				}

				lastX = hbox.getLinearVelocity().x;
			}
		});
		return hbox;
	}
}
