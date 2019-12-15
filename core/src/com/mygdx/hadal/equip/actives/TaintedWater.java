package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class TaintedWater extends ActiveItem {

	private final static String name = "Tainted Water";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	private final static int poisonRadius = 300;
	private final static float poisonDamage = 30/60f;
	private final static float poisonDuration = 4.0f;
	
	public TaintedWater(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {		
		new Poison(state, poisonRadius, poisonRadius,
				(int)(user.getSchmuck().getPosition().x * PPM), 
				(int)(user.getSchmuck().getPosition().y * PPM), 
				poisonDamage, poisonDuration, user.getSchmuck(), true, user.getSchmuck().getHitboxfilter());
	}
}
