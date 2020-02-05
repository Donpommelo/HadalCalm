package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class GrenadeLauncher extends RangedWeapon {

	private final static int clipSize = 6;
	private final static int ammoSize = 28;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.5f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 15.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 25.0f;
	private final static Vector2 projectileSize = new Vector2(25, 25);
	private final static float lifespan = 1.5f;
	
	private final static int explosionRadius = 150;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 25.0f;

	private final static Sprite weaponSprite = Sprite.MT_GRENADE;
	private final static Sprite eventSprite = Sprite.P_GRENADE;
	
	public GrenadeLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		WeaponUtils.createGrenade(state, startPosition, projectileSize, user, baseDamage, knockback, lifespan, startVelocity, true, explosionRadius, explosionDamage, explosionKnockback, filter);	
	}
}