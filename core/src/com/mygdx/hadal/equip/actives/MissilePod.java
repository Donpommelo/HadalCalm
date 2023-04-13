package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.HomingMissile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Fatatron Falpudding
 */
public class MissilePod extends ActiveItem {

	private static final float MAX_CHARGE = 15.0f;
	
	private static final float DURATION = 1.2f;
	private static final float PROC_CD = 0.1f;

	public MissilePod(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		user.addStatus(new Status(state, DURATION, false, user, user) {
			
			private float procCdCount;
			private int missileNum;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					missileNum++;

					SyncedAttack.HOMING_MISSILE.initiateSyncedAttackSingle(state, inflicted.getSchmuck(),
							inflicted.getSchmuck().getPixelPosition(), new Vector2(0, 5), missileNum);

				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD)),
				String.valueOf((int) HomingMissile.TORPEDO_EXPLOSION_DAMAGE)};
	}
}
