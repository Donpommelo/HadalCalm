package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class MeteorStrike extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 22.0f;

	private final static float baseDamage = 24.0f;
	private final static float knockback = 6.0f;
	
	private final static float meteorDuration = 3.0f;
	private final static float meteorInterval = 0.1f;
	private final static float spread = 15.0f;
	
	
	public MeteorStrike(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createMeteors(state, new Vector2(mouseLocation).scl(1 / PPM), user.getPlayer(), meteorDuration, meteorInterval, spread, baseDamage, knockback);
	}
	
	@Override
	public float getUseDuration() { return meteorDuration; }
}
