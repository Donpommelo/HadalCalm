package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Boomerang extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 24;
	private static final float shootCd = 0.75f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 0.75f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 35.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 70.0f;
	private static final Vector2 projectileSize = new Vector2(60, 60);
	private static final float lifespan = 2.0f;
	private static final float returnAmp = 5.0f;
	private static final float boomerangRotationSpeed = 10.0f;

	private static final Sprite projSprite = Sprite.BOOMERANG;
	private static final Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private static final Sprite eventSprite = Sprite.P_BOOMERANG;

	public Boomerang(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BOOMERANG.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createBoomerang(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.BOOMERANG_WHIZ.playSourced(state, startPosition, 1.0f);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);
		hbox.setRestitution(0.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.BOOMERANG,
				DamageTag.WHACKING, DamageTag.RANGED).setRepeatable(true));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ReturnToUser(state, hbox, user.getBodyData(), hbox.getStartVelo().len() * returnAmp));
		hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.WOOSH, 0.5f, true).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE1, 0.75f, true).setSynced(false));
		hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), boomerangRotationSpeed));

		return hbox;
	}

	@Override
	public float getBotRangeMax() {
		return 24.0f;
	}
}
