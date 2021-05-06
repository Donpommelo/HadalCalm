package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class AbyssalInsignia extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float hpThreshold = 0.2f;
	private static final float bonusAttackSpeed = 0.45f;
	
	public AbyssalInsignia() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onShoot(Equippable tool) {
				if (inflicter.getCurrentHp() <= inflicter.getStat(Stats.MAX_HP) * hpThreshold) {
					float cooldown = inflicter.getSchmuck().getShootCdCount();
					inflicter.getSchmuck().setShootCdCount(cooldown * (1 - bonusAttackSpeed));
				}
			}
		};
		return enchantment;
	}
}
