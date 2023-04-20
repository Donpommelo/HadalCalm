package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Kineater extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float HP_DRAIN_PERCENT = 0.15f;
	
	public Kineater() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void afterBossSpawn(Enemy boss) { 
				SoundEffect.MAGIC27_EVIL.playUniversal(state, boss.getPixelPosition(), 1.0f, false);
				boss.getBodyData().setCurrentHp(boss.getBodyData().getCurrentHp() * (1.0f - HP_DRAIN_PERCENT));
			}
		}.setServerOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (HP_DRAIN_PERCENT * 100))};
	}
}
