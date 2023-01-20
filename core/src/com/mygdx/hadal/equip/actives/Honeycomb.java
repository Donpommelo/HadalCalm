package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Whatditya Wheppermint
 */
public class Honeycomb extends ActiveItem {

	private static final float MAX_CHARGE = 15.0f;

	private static final float PROJECTILE_SPEED = 5.0f;
	private static final float DURATION = 1.5f;
	private static final float PROC_CD = 0.15f;

	public Honeycomb(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		user.addStatus(new Status(state, DURATION, false, user, user) {

			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					SyncedAttack.BEE_HONEYCOMB.initiateSyncedAttackSingle(state, user.getPlayer(), user.getSchmuck().getPixelPosition(),
							new Vector2(0, PROJECTILE_SPEED));
				}
				procCdCount += delta;
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD))};
	}
}
