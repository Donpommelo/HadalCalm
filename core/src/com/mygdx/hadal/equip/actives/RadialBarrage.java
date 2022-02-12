package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Glorseradish Glabberish
 */
public class RadialBarrage extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 20.0f;
	
	private static final float duration = 5.0f;
	private static final float procCd = 0.1f;
	private static final int totalShots = 6;

	public RadialBarrage(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Vector2 angle = new Vector2(1, 0);
		
		if (user.getCurrentTool() instanceof RangedWeapon) {
			angle.scl(((RangedWeapon) user.getCurrentTool()).getProjectileSpeed());

			user.addStatus(new Status(state, duration, false, user, user) {
				
				private float procCdCount;
				private int shotsFired;
				
				@Override
				public void timePassing(float delta) {
					super.timePassing(delta);
					if (procCdCount >= procCd && shotsFired < totalShots) {
						procCdCount -= procCd;
						shotsFired++;

						angle.setAngleDeg(angle.angleDeg() + 360.0f / totalShots);
						user.getCurrentTool().fire(state, user.getSchmuck(), user.getSchmuck().getPixelPosition(), new Vector2(angle), user.getSchmuck().getHitboxfilter());
					}
					procCdCount += delta;
				}
			});
			
			int clipSize = user.getCurrentTool().getClipSize();
			
			if (clipSize > 1) {
				gainChargeByPercent(0.20f);
			}
			if (clipSize > 3) {
				gainChargeByPercent(0.20f);
			}
			if (clipSize > 6) {
				gainChargeByPercent(0.20f);
			}
			if (clipSize > 9) {
				gainChargeByPercent(0.30f);
			}
		}
	}
}
