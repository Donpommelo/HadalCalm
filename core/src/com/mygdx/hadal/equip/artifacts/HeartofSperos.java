package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class HeartofSperos extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private final static float costReduction = 0.5f;
	
	public HeartofSperos() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public void onShoot(Equipable tool) {
				
				float fuelCost = inflicted.getStat(Stats.MAX_FUEL) / tool.getClipSize() * costReduction;
				
				if (fuelCost <= inflicted.getCurrentFuel() && inflicted instanceof PlayerBodyData) {
					tool.gainClip(1);
					
					((PlayerBodyData)inflicted).fuelSpend(fuelCost);
				}
			}
		});
		return enchantment;
	}
}
