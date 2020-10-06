package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactBlockProjectiles;

public class SenescentShield extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float knockbackProj = 10.0f;

	public SenescentShield() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				
				if (!hbox.isEffectsHit()) { return; } 
				
				hbox.addStrategy(new ContactBlockProjectiles(state, hbox, b, knockbackProj));
			}
		});
		
		return enchantment;
	}
}
