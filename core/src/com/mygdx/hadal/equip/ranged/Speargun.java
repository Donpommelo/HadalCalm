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
import com.mygdx.hadal.strategies.hitbox.*;

public class Speargun extends RangedWeapon {

	private static final int clipSize = 8;
	private static final int ammoSize = 88;
	private static final float shootCd = 0.2f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 40.0f;
	private static final float nerfedDamage = 30.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 15.0f;
	private static final float projectileSpeed = 35.0f;
	private static final Vector2 projectileSize = new Vector2(50, 12);
	private static final float lifespan = 1.0f;

	private static final Sprite projSprite = Sprite.HARPOON;
	private static final Sprite weaponSprite = Sprite.MT_SPEARGUN;
	private static final Sprite eventSprite = Sprite.P_SPEARGUN;
	
	public Speargun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.SPEAR.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createSpear(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, boolean nerfed) {
		SoundEffect.SPIKE.playSourced(state, startPosition, 0.5f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);
		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), nerfed ? nerfedDamage : baseDamage, knockback,
				DamageSource.SPEARGUN, DamageTag.POKING, DamageTag.RANGED));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.8f, true).setSynced(false));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_DIRT_HIT, 0.6f).setSynced(false));

		return hbox;
	}
}
