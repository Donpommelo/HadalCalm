package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.UnlocktoItem;

public class OlFaithful extends Artifact {

	private static final int SLOT_COST = 3;

	public OlFaithful() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void playerCreate(boolean reset) {
				if (state.getMode().isHub() || !reset) { return; }

				UnlockEquip savedEquip = null;
				if (p.getPlayer().getUser() == HadalGame.usm.getOwnUser()) {
					savedEquip = UnlockEquip.getByName(JSONManager.loadout.getEquip()[0]);
				} else {
					if (p.getPlayer().getUser() != null) {
						savedEquip = p.getPlayer().getUser().getEffectManager().getLastEquippedPrimary();
					}
				}
				if (savedEquip != null) {
					if (savedEquip != UnlockEquip.NOTHING) {
						p.getPlayer().getEquipHelper().getMultitools()[0] = UnlocktoItem.getUnlock(UnlockEquip.NOTHING, null);
						p.getPlayer().getEquipHelper().pickup(UnlocktoItem.getUnlock(savedEquip, null));
					}
				}
			}
		});
	}
}
