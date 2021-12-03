package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class AbyssalInsignia extends Artifact {

	private static final int slotCost = 1;
	
	private static final float hpThreshold = 0.5f;
	private static final float bonusAttackSpeedMax = 0.5f;
	private static final float bonusAttackSpeedMin = 0.1f;

	public AbyssalInsignia() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onShoot(Equippable tool) {
				float hpPercent = inflicter.getCurrentHp() / inflicter.getStat(Stats.MAX_HP);
				if (hpPercent < hpThreshold) {
					float bonusAttackSpeed = bonusAttackSpeedMax - hpPercent / hpThreshold * (bonusAttackSpeedMax - bonusAttackSpeedMin);
					float cooldown = inflicter.getSchmuck().getShootCdCount();
					System.out.println(bonusAttackSpeed);
					inflicter.getSchmuck().setShootCdCount(cooldown * (1 - bonusAttackSpeed));
				}
			}
		};
	}
}
