package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Gludelaire Grancisco
 */
public class HealingField extends ActiveItem {

	private static final float MAX_CHARGE = 20.0f;
	
	private static final float FIELD_SIZE = 360.0f;
	private static final float FIELD_HEAL = 0.2f;
	private static final float HEAL_DURATION = 10.0f;
	
	public HealingField(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.HEALING_FIELD.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), true,
				FIELD_SIZE, FIELD_HEAL, HEAL_DURATION);
	}
	
	@Override
	public float getUseDuration() { return HEAL_DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) HEAL_DURATION),
				String.valueOf((int) (FIELD_HEAL * 60))};
	}
}
