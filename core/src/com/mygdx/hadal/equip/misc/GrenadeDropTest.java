package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

public class GrenadeDropTest extends RangedWeapon {

	private final static String name = "Grenade Launcher";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 0.0f;
	private final static int projectileWidth = 25;
	private final static float lifespan = 3.0f;
	private final static float gravity = 1;
	private final static float restitution = 0.3f;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 25.0f;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			WeaponUtils.createGrenade(state, x, y, user, tool, baseDamage, knockback, projectileWidth, gravity, lifespan, restitution, projDura,
					startVelocity, false, explosionRadius, explosionDamage, explosionKnockback, filter);
		}
	};
	
	public GrenadeDropTest(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}
}
