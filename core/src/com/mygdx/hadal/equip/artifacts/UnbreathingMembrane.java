package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class UnbreathingMembrane extends Artifact {

	private static final int slotCost = 1;
	
	private static final float spdReduction = -0.75f;
	private static final float bonusClip = 1.0f;
	private static final float bonusRecoil = 3.0f;
	
	public UnbreathingMembrane() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.GROUND_SPD, spdReduction, p),
				new StatChangeStatus(state, Stats.AIR_SPD, spdReduction, p),
				new StatChangeStatus(state, Stats.JUMP_POW, spdReduction, p),
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClip, p),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, bonusRecoil, p),
				new Status(state, p) {
			
					@Override
					public void onReloadFinish(Equippable tool) {
						if (p.getCurrentTool() instanceof RangedWeapon weapon) {
							weapon.gainAmmo(1.0f);
						}
					}
				});
	}
}
