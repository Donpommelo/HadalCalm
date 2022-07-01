package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class FeelingofBeingWatched extends Artifact {

	private static final int slotCost = 1;
	
	private static final float fuelThreshold = 5.0f;
	private static final float damageBuff = 0.4f;
	private static final float buffDuration = 3.0f;
	private static final float buffCooldown = 6.0f;

	public FeelingofBeingWatched() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount = buffCooldown;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < buffCooldown) {
					procCdCount += delta;
				}

				if (procCdCount >= buffCooldown && p.getCurrentFuel() <= fuelThreshold) {
					procCdCount -= buffCooldown;

					p.getPlayer().setShader(Shader.PULSE_RED, buffDuration, false);
					p.addStatus(new StatChangeStatus(state, Stats.DAMAGE_AMP, damageBuff, p));
				}
			}

		}.setClientIndependent(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (damageBuff * 100)),
				String.valueOf((int) buffDuration),
				String.valueOf((int) buffCooldown) };
	}
}
