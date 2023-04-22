package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class FeelingofBeingWatched extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float FUEL_THRESHOLD = 5.0f;
	private static final float DAMAGE_BUFF = 0.4f;
	private static final float BUFF_DURATION = 3.0f;
	private static final float BUFF_COOLDOWN = 6.0f;

	public FeelingofBeingWatched() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount = BUFF_COOLDOWN;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < BUFF_COOLDOWN) {
					procCdCount += delta;
				}

				if (procCdCount >= BUFF_COOLDOWN && p.getCurrentFuel() <= FUEL_THRESHOLD) {
					procCdCount -= BUFF_COOLDOWN;

					p.getPlayer().setShader(Shader.PULSE_RED, BUFF_DURATION);
					p.addStatus(new StatChangeStatus(state, BUFF_DURATION, Stats.DAMAGE_AMP, DAMAGE_BUFF, p, p));
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_BUFF * 100)),
				String.valueOf((int) BUFF_DURATION),
				String.valueOf((int) BUFF_COOLDOWN) };
	}
}
