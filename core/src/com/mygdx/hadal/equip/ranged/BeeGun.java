package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class BeeGun extends RangedWeapon {

	private static final int clipSize = 20;
	private static final int ammoSize = 96;
	private static final float shootCd = 0.4f;
	private static final float reloadTime = 1.9f;
	private static final int reloadAmount = 0;
	private static final float projectileSpeedStart = 20.0f;
	private static final Vector2 projectileSize = new Vector2(20, 18);
	public static final float lifespan = 5.0f;
	private static final int beeSpread = 25;
	private static final float beeHoming = 90;

	private static final float beeBaseDamage = 6.0f;
	private static final float beeKnockback = 8.0f;
	private static final int beeDurability = 5;

	private static final Sprite projSprite = Sprite.BEE;
	private static final Sprite weaponSprite = Sprite.MT_BEEGUN;
	private static final Sprite eventSprite = Sprite.P_BEEGUN;

	private static final int homeRadius = 30;

	public BeeGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeedStart, shootCd, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BEE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createBee(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, DamageSource source) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);
		hbox.setDensity(0.5f);
		hbox.setDurability(beeDurability);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), beeBaseDamage, beeKnockback, source,
				DamageTag.BEES, DamageTag.RANGED).setRepeatable(true));
		hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), beeHoming, homeRadius).setDisruptable(true));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), beeSpread));
		hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.BEE_BUZZ, 0.6f, true)
		.setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) beeBaseDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
