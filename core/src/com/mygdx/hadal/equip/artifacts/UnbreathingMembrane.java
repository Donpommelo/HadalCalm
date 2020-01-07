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
	
	private final static float spdReduction = -0.75f;
	private final static float bonusClip = 1.0f;
	private final static float bonusRecoil = 3.0f;
	
	public UnbreathingMembrane() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.GROUND_SPD, spdReduction, b), 
				new StatChangeStatus(state, Stats.AIR_SPD, spdReduction, b), 
				new StatChangeStatus(state, Stats.JUMP_POW, spdReduction, b),
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClip, b),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, bonusRecoil, b),
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
