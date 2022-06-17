package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

public class Gemmule extends Artifact {

	private static final int slotCost = 1;
	private static final float bonusInvulnerability = 8.0f;
	
	public Gemmule() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void playerCreate() {
				p.addStatus(new Invulnerability(state, bonusInvulnerability, p, p));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) bonusInvulnerability)};
	}
}
