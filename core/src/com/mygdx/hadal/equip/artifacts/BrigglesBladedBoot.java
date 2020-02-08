package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class BrigglesBladedBoot extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float bonusFastFallPow = 1.5f;

	public BrigglesBladedBoot() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.FASTFALL_POW, bonusFastFallPow, b) {
			
			@Override
			public void onInflict() {
				((Player)inflicted.getSchmuck()).setStomping(true);
			}
			
			@Override
			public void onRemove() {
				((Player)inflicted.getSchmuck()).setStomping(false);
			}
		};
		return enchantment;
	}
}
