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

	private static final int SLOT_COST = 1;
	
	private static final float SPD_REDUCTION = -0.75f;
	private static final float BONUS_CLIP = 1.0f;
	private static final float BONUS_RECOIL = 3.0f;
	
	public UnbreathingMembrane() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.GROUND_SPD, SPD_REDUCTION, p),
				new StatChangeStatus(state, Stats.AIR_SPD, SPD_REDUCTION, p),
				new StatChangeStatus(state, Stats.JUMP_POW, SPD_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_CLIP, BONUS_CLIP, p),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, BONUS_RECOIL, p),
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
