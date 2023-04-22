package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PlayerBounce extends Artifact {

	private static final int SLOT_COST = 0;
	private static final float BOUNCE = 1.0f;

	public PlayerBounce() { super(SLOT_COST); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() != null) {
					p.getSchmuck().setRestitution(BOUNCE);
				} else {
					p.getPlayer().setRestitutionModifier(BOUNCE);
				}
			}

			@Override
			public void onRemove() {
				p.getSchmuck().setRestitution(0.0f);
			}
		};
	}
}
