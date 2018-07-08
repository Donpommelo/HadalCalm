package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class MissilePod extends ActiveItem {

	private final static String name = "Missile Pod";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 12.0f;
	
	public MissilePod(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createHomingTorpedo(state, 
				user.getSchmuck().getBody().getPosition().x * PPM, 
				user.getSchmuck().getBody().getPosition().y * PPM,
				user.getSchmuck(), user.getCurrentTool(), 6, 60, new Vector2(1, 1), false, user.getSchmuck().getHitboxfilter());	
	}

}
