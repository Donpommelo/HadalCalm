package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class HeartofSperos extends Artifact {

	private static final int slotCost = 3;
	
	private static final float costReduction = 0.6f;
	
	public HeartofSperos() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void onShoot(Equippable tool) {
				float fuelCost = p.getStat(Stats.MAX_FUEL) / tool.getClipSize() * costReduction;
				
				if (fuelCost <= p.getCurrentFuel()) {
					tool.gainClip(1);
					p.fuelSpend(fuelCost);
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (costReduction * 100))};
	}
}
