package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class RadialBarrage extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 15.0f;
	
	public RadialBarrage(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Vector2 angle = new Vector2(1, 0);
		
		if (user.getCurrentTool() instanceof RangedWeapon) {
			angle.scl(((RangedWeapon)user.getCurrentTool()).getProjectileSpeed());
			
			for (int i = 0; i < 8; i++) {
				angle.setAngle(angle.angle() + 45);
				user.getCurrentTool().fire(state, user.getSchmuck(), user.getSchmuck().getPixelPosition(), new Vector2(angle), user.getSchmuck().getHitboxfilter());
			}
			
			int clipSize = ((RangedWeapon)user.getCurrentTool()).getClipSize();
			
			if (clipSize > 2) {
				gainChargeByPercent(0.25f);
			}
			if (clipSize > 6) {
				gainChargeByPercent(0.25f);
			}
			if (clipSize > 12) {
				gainChargeByPercent(0.25f);
			}
		}
	}
}
