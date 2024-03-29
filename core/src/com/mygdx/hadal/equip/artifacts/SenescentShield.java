package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactBlockProjectiles;

public class SenescentShield extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float KNOCKBACK_PROJ = 10.0f;

	public SenescentShield() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }

				hbox.addStrategy(new ContactBlockProjectiles(state, hbox, p, KNOCKBACK_PROJ));
			}
		});
	}
}
