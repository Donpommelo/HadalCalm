package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.UnlocktoItem;

public class
HumanityModule extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusActiveCharge = 0.5f;
	
	public HumanityModule() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, bonusActiveCharge, p),
				new Status(state, p) {
			
			@Override
			public void afterActiveItem(ActiveItem tool) {
				ActiveItem item = UnlocktoItem.getUnlock(UnlockActives.getRandItemFromPool(state, ""), null);
				if (item != null) {
					p.pickup(item);
					item.setCurrentCharge(0.0f);
				}
			}
		});
	}
}
