package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

public class BeeGun extends RangedWeapon {

	private final static String name = "Bee Gun";
	private final static int clipSize = 28;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 2.1f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeedStart = 4.0f;

	private final static int spread = 45;
	
	private final static Sprite weaponSprite = Sprite.MT_BEEGUN;
	private final static Sprite eventSprite = Sprite.P_BEEGUN;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			WeaponUtils.createBees(state, x, y, user, tool, 1, spread, startVelocity, true, filter);
		}	
	};
	
	public BeeGun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weaponSprite, eventSprite);
	}

}
