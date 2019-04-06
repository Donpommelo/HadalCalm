package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Honeycomb extends ActiveItem {

	private final static String name = "Honeycomb";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 9.0f;
	
	public Honeycomb(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createBees(state, 
				user.getSchmuck().getPosition().x * PPM, 
				user.getSchmuck().getPosition().y * PPM, 
				user.getSchmuck(), user.getCurrentTool(), 5, 180, new Vector2(1, 1), false, user.getSchmuck().getHitboxfilter());
	}

}
