package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class MissilePod extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 15.0f;
	
	private final static float duration = 1.2f;
	
	private static final float procCd = .1f;
	private static final float damage = 18.0f;
	
	public MissilePod(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		user.addStatus(new Status(state, duration, false, user, user) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					WeaponUtils.createHomingTorpedo(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), damage, 1, 15, new Vector2(0, 5), false, inflicted.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
