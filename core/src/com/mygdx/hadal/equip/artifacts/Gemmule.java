package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

public class Gemmule extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_INVULNERABILITY = 8.0f;
	
	public Gemmule() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void playerCreate() {
				p.addStatus(new Invulnerability(state, BONUS_INVULNERABILITY, p, p));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BONUS_INVULNERABILITY)};
	}
}
