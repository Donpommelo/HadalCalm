package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class NauticalMine extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 8.0f;
	
	private final static float projectileSize = 120;
	private final static float lifespan = 12.0f;
	
	private final static int explosionRadius = 400;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 40.0f;
	
	private final static float projectileSpeed = 15.0f;
	
	public NauticalMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LAUNCHER.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		WeaponUtils.createNauticalMine(state, user.getPlayer().getPixelPosition(), user.getPlayer(), new Vector2(weaponVelo).nor().scl(projectileSpeed), projectileSize, lifespan, explosionDamage, explosionKnockback, explosionRadius);
	}
}
