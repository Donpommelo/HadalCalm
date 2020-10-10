package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class GrenadeLauncher extends RangedWeapon {

	private static final int clipSize = 6;
	private static final int ammoSize = 28;
	private static final float shootCd = 0.25f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.5f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 0.0f;
	private static final float projectileSpeed = 28.0f;
	private static final Vector2 projectileSize = new Vector2(25, 25);
	private static final float lifespan = 2.5f;
	
	private static final int explosionRadius = 150;
	private static final float explosionDamage = 45.0f;
	private static final float explosionKnockback = 25.0f;

	private static final Sprite weaponSprite = Sprite.MT_GRENADE;
	private static final Sprite eventSprite = Sprite.P_GRENADE;
	
	public GrenadeLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LAUNCHER.playUniversal(state, startPosition, 0.25f, false);

		WeaponUtils.createGrenade(state, startPosition, projectileSize, user, baseDamage, knockback, lifespan, startVelocity, true, explosionRadius, explosionDamage, explosionKnockback, filter);	
	}
}