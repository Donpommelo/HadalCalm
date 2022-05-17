package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Objects;

public class OlFaithful extends Artifact {

	private static final int slotCost = 3;

	public OlFaithful() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void playerCreate() {
				if (state.getMode().isHub()) { return; }

				UnlockEquip savedEquip = null;
				if (p.getPlayer().equals(state.getPlayer())) {
					savedEquip = UnlockEquip.getByName(state.getGsm().getLoadout().getEquips()[0]);
				} else {
					if (p.getPlayer().getUser() != null) {
						savedEquip = p.getPlayer().getUser().getLastEquippedPrimary();
					}
				}
				if (savedEquip != null) {
					if (savedEquip != UnlockEquip.NOTHING) {
						p.getMultitools()[0] = Objects.requireNonNull(UnlocktoItem.getUnlock(UnlockEquip.NOTHING, null));
						p.pickup(Objects.requireNonNull(UnlocktoItem.getUnlock(savedEquip, null)));
					}
				}
			}
		});
	}
}
