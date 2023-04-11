package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class AbyssalInsignia extends Artifact {

	private static final int slotCost = 1;
	
	private static final float hpThreshold = 0.5f;
	private static final float bonusAttackSpeedMax = 0.5f;
	private static final float bonusAttackSpeedMin = 0.1f;

	private static final float shaderCount = 0.5f;

	public AbyssalInsignia() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < shaderCount) {
					procCdCount += delta;
				}
				float hpPercent = inflicter.getCurrentHp() / inflicter.getStat(Stats.MAX_HP);
				if (hpPercent < hpThreshold) {
						p.getPlayer().setShader(Shader.PULSE_RED_HP, shaderCount * 2);
				}
			}

			@Override
			public void onShoot(Equippable tool) {
				float hpPercent = inflicter.getCurrentHp() / inflicter.getStat(Stats.MAX_HP);
				if (hpPercent < hpThreshold) {
					float bonusAttackSpeed = bonusAttackSpeedMax - hpPercent / hpThreshold * (bonusAttackSpeedMax - bonusAttackSpeedMin);
					float cooldown = p.getPlayer().getShootHelper().getShootCdCount();
					p.getPlayer().getShootHelper().setShootCdCount(cooldown * (1 - bonusAttackSpeed));
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusAttackSpeedMin * 100)),
				String.valueOf((int) (hpThreshold * 100)),
				String.valueOf((int) (bonusAttackSpeedMax * 100))};
	}
}
