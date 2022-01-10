package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Lothdoof Lepomelo
 */
public class SpiritRelease extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 10.0f;
	
	public SpiritRelease(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		Vector2[] positions = new Vector2[3];
		positions[0] = new Vector2(user.getPlayer().getPixelPosition()).add(0, 100);
		positions[1] = new Vector2(user.getPlayer().getPixelPosition()).add(100, 0);
		positions[2] = new Vector2(user.getPlayer().getPixelPosition()).add(-100, 0);
		SyncedAttack.VENGEFUL_SPIRIT.initiateSyncedAttackMulti(state, user.getPlayer(), new Vector2(), positions, new Vector2[]{});
	}
}
