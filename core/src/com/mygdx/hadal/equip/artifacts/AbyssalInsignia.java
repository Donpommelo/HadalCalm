package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class AbyssalInsignia extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float HP_THRESHOLD = 0.5f;
	private static final float BONUS_ATTACK_SPEED_MAX = 0.5f;
	private static final float BONUS_ATTACK_SPEED_MIN = 0.1f;

	private static final float SHADER_COUNT = 0.5f;

	public AbyssalInsignia() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < SHADER_COUNT) {
					procCdCount += delta;
				}
				float hpPercent = inflicter.getCurrentHp() / inflicter.getStat(Stats.MAX_HP);

				if (hpPercent < HP_THRESHOLD) {
					p.getPlayer().setShader(Shader.PULSE_RED_HP, SHADER_COUNT * 2);
				}
			}

			@Override
			public void onShoot(Equippable tool) {
				float hpPercent = inflicter.getCurrentHp() / inflicter.getStat(Stats.MAX_HP);
				if (hpPercent < HP_THRESHOLD) {
					float bonusAttackSpeed = BONUS_ATTACK_SPEED_MAX - hpPercent / HP_THRESHOLD * (BONUS_ATTACK_SPEED_MAX - BONUS_ATTACK_SPEED_MIN);
					float cooldown = p.getPlayer().getShootHelper().getShootCdCount();
					p.getPlayer().getShootHelper().setShootCdCount(cooldown * (1 - bonusAttackSpeed));
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_ATTACK_SPEED_MIN * 100)),
				String.valueOf((int) (HP_THRESHOLD * 100)),
				String.valueOf((int) (BONUS_ATTACK_SPEED_MAX * 100))};
	}
}
