package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class UnbreathingMembrane extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	public UnbreathingMembrane() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.GROUND_SPD, -0.75f, b), 
				new StatChangeStatus(state, Stats.GROUND_ACCEL, -0.75f, b),
				new StatChangeStatus(state, Stats.AIR_SPD, -0.75f, b), 
				new StatChangeStatus(state, Stats.AIR_ACCEL, -0.75f, b),
				new StatChangeStatus(state, Stats.JUMP_POW, -0.75f, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, 0.75f, b),
				new StatChangeStatus(state, Stats.RANGED_CLIP, 0.75f, b),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, 5.0f, b),
				new Status(state, b) {
			
					@Override
					public void onReload(Equipable tool) {
						if (this.inflicted instanceof PlayerBodyData) {
							if (((PlayerBodyData)this.inflicted).getCurrentTool() instanceof RangedWeapon) {
								RangedWeapon weapon = (RangedWeapon)((PlayerBodyData)this.inflicted).getCurrentTool();
								weapon.gainAmmo(1.0f);
							}
						}
					}
				});
		return enchantment;
	}
}
