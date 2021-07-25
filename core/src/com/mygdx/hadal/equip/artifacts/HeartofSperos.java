package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class HeartofSperos extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;
	
	private static final float costReduction = 0.65f;
	
	public HeartofSperos() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public void onShoot(Equippable tool) {
				
				float fuelCost = inflicted.getStat(Stats.MAX_FUEL) / tool.getClipSize() * costReduction;
				
				if (fuelCost <= inflicted.getCurrentFuel() && inflicted instanceof PlayerBodyData) {
					tool.gainClip(1);
					
					((PlayerBodyData) inflicted).fuelSpend(fuelCost);
				}
			}
		});
		return enchantment;
	}
}
