package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Glorseradish Glabberish
 */
public class RadialBarrage extends ActiveItem {

	private static final float MAX_CHARGE = 20.0f;
	
	private static final float DURATION = 5.0f;
	private static final float PROC_CD = 0.1f;
	private static final int TOTAL_SHOTS = 6;

	public RadialBarrage(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Vector2 angle = new Vector2(1, 0);
		Equippable currentTool = user.getPlayer().getEquipHelper().getCurrentTool();
		if (currentTool instanceof RangedWeapon rangedWeapon) {
			angle.scl(rangedWeapon.getProjectileSpeed());

			user.addStatus(new Status(state, DURATION, false, user, user) {
				
				private float procCdCount;
				private int shotsFired;
				@Override
				public void timePassing(float delta) {
					super.timePassing(delta);
					if (procCdCount >= PROC_CD && shotsFired < TOTAL_SHOTS) {
						procCdCount -= PROC_CD;
						shotsFired++;

						angle.setAngleDeg(angle.angleDeg() + 360.0f / TOTAL_SHOTS);
						currentTool.fire(state, user.getPlayer(), user.getPlayer().getPixelPosition(), new Vector2(angle), user.getSchmuck().getHitboxFilter());
					}
					procCdCount += delta;
				}
			});
			
			int clipSize = currentTool.getClipSize();
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

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(TOTAL_SHOTS),
				String.valueOf(PROC_CD)};
	}
}
