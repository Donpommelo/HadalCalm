package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

public class Gemmule extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float bonusInvulnerability = 8.0f;
	
	public Gemmule() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void playerCreate() {
				inflicted.addStatus(new Invulnerability(state, bonusInvulnerability, inflicted, inflicted));
			}
		};
		return enchantment;
	}
}
