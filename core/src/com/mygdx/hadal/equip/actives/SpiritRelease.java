package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class SpiritRelease extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	private static final float spiritDamage= 40.0f;
	private static final float spiritKnockback= 25.0f;
	private static final float spiritLifespan= 7.5f;
	
	private Vector2 spiritPos = new Vector2();
	
	public SpiritRelease(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		spiritPos.set(user.getPlayer().getPixelPosition()).add(0, 100);
		WeaponUtils.releaseVengefulSpirits(state, new Vector2(spiritPos), spiritLifespan, spiritDamage, spiritKnockback, user, user.getPlayer().getHitboxfilter());
		spiritPos.set(user.getPlayer().getPixelPosition()).add(100, 0);
		WeaponUtils.releaseVengefulSpirits(state, new Vector2(spiritPos), spiritLifespan, spiritDamage, spiritKnockback, user, user.getPlayer().getHitboxfilter());
		spiritPos.set(user.getPlayer().getPixelPosition()).add(-100, 0);
		WeaponUtils.releaseVengefulSpirits(state, new Vector2(spiritPos), spiritLifespan, spiritDamage, spiritKnockback, user, user.getPlayer().getHitboxfilter());
	}
}
