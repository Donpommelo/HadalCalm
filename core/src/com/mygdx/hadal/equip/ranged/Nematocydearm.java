package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class Nematocydearm extends RangedWeapon {

	private static final int clipSize = 5;
	private static final int ammoSize = 40;
	private static final float shootCd = 0.1f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.9f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 33.0f;
	private static final float recoil = 2.0f;
	private static final float knockback = 20.0f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(91, 35);
	private static final Vector2 stickySize = new Vector2(70, 25);
	private static final float lifespan = 7.0f;
	
	private static final int spread = 5;

	private static final Sprite projSprite = Sprite.NEMATOCYTE;
	private static final Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite eventSprite = Sprite.P_NEMATOCYTEARM;
	
	public Nematocydearm(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.NEMATOCYTE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createNematocyte(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.ATTACK1.playSourced(state, startPosition, 0.4f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, stickySize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);
		hbox.setSpriteSize(projectileSize);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.NEMATOCYDEARM,
				DamageTag.POKING, DamageTag.RANGED).setStaticKnockback(true));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.6f, true).setSynced(false));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DANGER_BLUE, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.SKY_BLUE).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, false));

		return hbox;
	}
}
