package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Kineater extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final float hpDrainPercent = 0.15f;
	
	public Kineater() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void afterBossSpawn(Enemy boss) { 
				boss.getBodyData().setCurrentHp(boss.getBodyData().getCurrentHp() - (boss.getBodyData().getStat(Stats.MAX_HP) * hpDrainPercent));
			}
		};
		return enchantment;
	}
}
