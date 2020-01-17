package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class RingofTesting extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 0;
	
	private final static float bonusProjSize = 1.0f;
	private final static float bonusProjLifespan = 0.5f;
	
	public RingofTesting() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, bonusProjSize, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLifespan, b),
				new Status(state, b) {
			
			@Override
			public void onInflict() {
				inflicted.getSchmuck().getBody().getFixtureList().get(1).setFriction(0.0f);
			}
			
			@Override
			public void onRemove() {
				inflicted.getSchmuck().getBody().getFixtureList().get(1).setFriction(1.0f);
			}
			
		});
		
		return enchantment;
	}
}
