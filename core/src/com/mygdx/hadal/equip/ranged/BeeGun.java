package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

public class BeeGun extends RangedWeapon {

	private final static String name = "Bee Gun";
	private final static int clipSize = 24;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 12.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 2.5f;
	private final static float projectileSpeedStart = 3.0f;
	private final static int projectileWidth = 23;
	private final static int projectileHeight = 21;
	private final static float lifespan = 5.0f;
	private final static float homeRadius = 10;
	
	private final static int spread = 45;
	
	private final static String weapSpriteId = "beegun";

	private static final float maxLinSpd = 100;
	private static final float maxLinAcc = 1000;
	private static final float maxAngSpd = 180;
	private static final float maxAngAcc = 90;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			WeaponUtils.createBees(state, x, y, user, tool, baseDamage, knockback, projectileWidth, projectileHeight, lifespan,
					1, spread, startVelocity,
					true, maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius, homeRadius, filter);
		}	
	};
	
	public BeeGun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
