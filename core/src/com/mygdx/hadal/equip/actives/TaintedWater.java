package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class TaintedWater extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 15.0f;
	
	private final static float duration = 2.0f;
	private static final float procCd = .25f;
	
	private final static Vector2 poisonSize = new Vector2(101, 250);
	private final static float poisonDamage = 1.0f;
	private final static float poisonDuration = 3.0f;
	
	public TaintedWater(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, final PlayerBodyData user) {	
		
		final boolean right = weaponVelo.x > 0;

		user.addStatus(new Status(state, duration, false, user, user) {
			
			private float procCdCount = procCd;
			private Vector2 poisonPos = new Vector2(user.getSchmuck().getPixelPosition());
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					new Poison(state, poisonPos.add(poisonSize.x * (right ? 1 : -1), 0), poisonSize, poisonDamage, poisonDuration, user.getSchmuck(), true, user.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
