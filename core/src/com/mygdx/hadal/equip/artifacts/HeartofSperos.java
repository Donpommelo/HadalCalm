package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class HeartofSperos extends Artifact {

	private static final int SLOT_COST = 3;
	
	private static final float COST_REDUCTION = 0.6f;
	private static final float FUEL_CD = 0.5f;

	public HeartofSperos() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void onShoot(Equippable tool) {
				float fuelCost = p.getStat(Stats.MAX_FUEL) / tool.getClipSize() * COST_REDUCTION;
				
				if (fuelCost <= p.getCurrentFuel()) {
					tool.gainClip(1);
					p.fuelSpend(fuelCost);
					p.getPlayer().getFuelHelper().setFuelRegenCdCount(FUEL_CD);
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (COST_REDUCTION * 100))};
	}
}
