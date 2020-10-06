package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Plumpowder extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	
	private static final float startCharge = 1.0f;
	public Plumpowder() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.STARTING_CHARGE, startCharge, b) {
			
			@Override
			public void playerCreate() {
				((Player) inflicted.getSchmuck()).getPlayerData().getActiveItem().setCurrentChargePercent(1.0f);
			}
		};
		return enchantment;
	}
}
