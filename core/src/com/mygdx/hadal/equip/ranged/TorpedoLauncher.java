package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class TorpedoLauncher extends RangedWeapon {

	private final static int clipSize = 4;
	private final static int ammoSize = 24;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.7f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 10.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 45.0f;
	private final static Vector2 projectileSize = new Vector2(60, 18);
	private final static float lifespan = 1.5f;
		
	private final static int explosionRadius = 150;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 25.0f;

	private final static Sprite weaponSprite = Sprite.MT_TORPEDO;
	private final static Sprite eventSprite = Sprite.P_TORPEDO;
	
	public TorpedoLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.ROCKET.playUniversal(state, startPosition, 0.5f, false);

		WeaponUtils.createTorpedo(state, startPosition, projectileSize, user, baseDamage, knockback, lifespan, startVelocity, true, explosionRadius, explosionDamage, explosionKnockback, filter);
	}
}
