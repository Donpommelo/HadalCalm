package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Whatditya Wheppermint
 */
public class Honeycomb extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 15.0f;
	private static final float projectileSpeed = 5.0f;
	private static final int homeRadius = 60;

	private static final float duration = 1.0f;
	private static final float procCd = 0.1f;

	public Honeycomb(Schmuck user) {
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
					WeaponUtils.createBees(state, user.getSchmuck().getPixelPosition(), user.getSchmuck(), 1,
						homeRadius, new Vector2(0, projectileSpeed), false, user.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		});
	}
}
