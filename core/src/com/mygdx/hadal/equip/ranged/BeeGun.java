package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class BeeGun extends RangedWeapon {

	private static final int clipSize = 20;
	private static final int ammoSize = 96;
	private static final float shootCd = 0.4f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.9f;
	private static final int reloadAmount = 0;
	private static final float recoil = 0.0f;
	private static final float projectileSpeedStart = 20.0f;

	private static final Sprite weaponSprite = Sprite.MT_BEEGUN;
	private static final Sprite eventSprite = Sprite.P_BEEGUN;

	private static final int homeRadius = 25;

	public BeeGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, 23);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		WeaponUtils.createBees(state, startPosition, user, 1, homeRadius, startVelocity, true, filter);
	}
}
