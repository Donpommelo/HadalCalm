package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class BeeGun extends RangedWeapon {

	private final static int clipSize = 24;
	private final static int ammoSize = 96;
	private final static float shootCd = 0.2f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.6f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeedStart = 15.0f;

	private final static Sprite weaponSprite = Sprite.MT_BEEGUN;
	private final static Sprite eventSprite = Sprite.P_BEEGUN;
	
	public BeeGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, 23);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		WeaponUtils.createBees(state, startPosition, user, 1, startVelocity, true, filter);
	}
}
