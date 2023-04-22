package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.VengefulSpirit;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Lothdoof Lepomelo
 */
public class SpiritRelease extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;

	private static final int SPIRIT_NUM = 3;

	public SpiritRelease(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		Vector2[] positions = new Vector2[SPIRIT_NUM];
		positions[0] = new Vector2(user.getPlayer().getPixelPosition()).add(0, 100);
		positions[1] = new Vector2(user.getPlayer().getPixelPosition()).add(100, 0);
		positions[2] = new Vector2(user.getPlayer().getPixelPosition()).add(-100, 0);
		SyncedAttack.VENGEFUL_SPIRIT.initiateSyncedAttackMulti(state, user.getPlayer(), new Vector2(), positions,
				new Vector2[]{});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(SPIRIT_NUM),
				String.valueOf((int) VengefulSpirit.SPIRIT_DEFAULT_DAMAGE)};
	}
}
