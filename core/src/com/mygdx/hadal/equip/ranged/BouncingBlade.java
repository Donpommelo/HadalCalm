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

public class BouncingBlade extends RangedWeapon {

	private static final int clipSize = 5;
	private static final int ammoSize = 30;
	private static final float shootCd = 0.7f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.4f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 42.0f;
	private static final float recoil = 6.0f;
	private static final float knockback = 25.0f;
	private static final float projectileSpeed = 59.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final float lifespan = 1.5f;
	private static final int durability = 5;

	private static final Sprite projSprite = Sprite.BUZZSAW;
	private static final Sprite weaponSprite = Sprite.MT_BLADEGUN;
	private static final Sprite eventSprite = Sprite.P_BLADEGUN;

	public BouncingBlade(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BOUNCING_BLADE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createBouncingBlade(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.METAL_IMPACT_1.playSourced(state, startPosition, 0.75f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);
		hbox.setDurability(durability);
		hbox.setRestitution(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.BOUNCING_BLADES,
				DamageTag.CUTTING, DamageTag.RANGED).setRepeatable(true));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.METAL_IMPACT_2, 0.4f).setSynced(false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE6, 0.5f, true).setSynced(false));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(durability),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
