package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.RemoveStrategy;

public class MachineGhost extends Artifact {

	private static final int SLOT_COST = 2;
	
	public MachineGhost() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new RemoveStrategy(state, hbox, p, ContactWallDie.class));
				hbox.setSensor(true);
			}
		});
	}
}
