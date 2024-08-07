package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class FeelingofBeingWatched extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float FUEL_THRESHOLD = 5.0f;
	private static final float BUFF_DURATION = 3.0f;
	private static final float BUFF_COOLDOWN = 6.0f;
	private static final int CRIT_AMOUNT = 1;

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

					p.getPlayer().getShaderHelper().setShader(Shader.PULSE_RED, BUFF_DURATION);
					p.addStatus(new Status(state, BUFF_DURATION, false, p, p) {

						@Override
						public int onCalcDealCrit(int crit, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
							return crit + CRIT_AMOUNT;
						}
					});
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BUFF_DURATION),
				String.valueOf((int) BUFF_COOLDOWN) };
	}
}
