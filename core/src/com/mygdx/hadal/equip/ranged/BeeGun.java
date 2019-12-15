package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class BeeGun extends RangedWeapon {

	private final static String name = "Bee Gun";
	private final static int clipSize = 24;
	private final static int ammoSize = 80;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeedStart = 4.0f;

	private final static int spread = 45;
	
	private final static Sprite weaponSprite = Sprite.MT_BEEGUN;
	private final static Sprite eventSprite = Sprite.P_BEEGUN;
	
	public BeeGun(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}

	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		WeaponUtils.createBees(state, x, y, user, this, 1, spread, startVelocity, true, filter);
	}
}
