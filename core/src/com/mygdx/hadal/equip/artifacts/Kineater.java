package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Kineater extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private final float hpDrainPercent = 0.15f;
	
	public Kineater() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void afterBossSpawn(Enemy boss) { 
				SoundEffect.MAGIC27_EVIL.playUniversal(state, boss.getPixelPosition(), 1.0f, false);
				boss.getBodyData().setCurrentHp(boss.getBodyData().getCurrentHp() * (1.0f - hpDrainPercent));
			}
		};
		return enchantment;
	}
}
