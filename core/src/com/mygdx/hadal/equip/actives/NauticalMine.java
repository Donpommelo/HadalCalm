package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Froginald Frugwump
 */
public class NauticalMine extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 8.0f;
	
	private static final float projectileSize = 120;
	private static final float lifespan = 12.0f;
	
	private static final int explosionRadius = 400;
	private static final float explosionDamage = 75.0f;
	private static final float explosionKnockback = 40.0f;
	
	private static final float projectileSpeed = 15.0f;
	
	public NauticalMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LAUNCHER.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		WeaponUtils.createNauticalMine(state, user.getPlayer().getPixelPosition(), user.getPlayer(),
			new Vector2(weaponVelo).nor().scl(projectileSpeed), projectileSize, lifespan,
			explosionDamage, explosionKnockback, explosionRadius, 1.0f, false);
	}
}
