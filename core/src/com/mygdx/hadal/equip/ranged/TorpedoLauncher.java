package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

public class TorpedoLauncher extends RangedWeapon {

	private final static String name = "Torpedo Launcher";
	private final static int clipSize = 4;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 100;
	private final static int projectileHeight = 20;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 25.0f;

	private final static Sprite weaponSprite = Sprite.MT_TORPEDO;
	private final static Sprite eventSprite = Sprite.P_TORPEDO;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			WeaponUtils.createTorpedo(state, x, y, user, tool, baseDamage, knockback, projectileWidth, projectileHeight, gravity, lifespan, projDura,
					startVelocity, true, explosionRadius, explosionDamage, explosionKnockback, filter);
		}
	};
	
	public TorpedoLauncher(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weaponSprite, eventSprite);
	}
}
