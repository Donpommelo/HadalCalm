package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.UnlocktoItem;

public class HumanityModule extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float bonusActiveCharge = 0.25f;
	
	public HumanityModule() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, bonusActiveCharge, b),
				new Status(state, b) {
			
			@Override
			public void afterActiveItem(ActiveItem tool) {

				ActiveItem item = UnlocktoItem.getUnlock(UnlockActives.valueOf(UnlockActives.getRandItemFromPool(state, "")), null);
				item.setCurrentCharge(0.0f);
				
				((Player)inflicted.getSchmuck()).getPlayerData().pickup(item);
			}
		});
		return enchantment;
	}
}
