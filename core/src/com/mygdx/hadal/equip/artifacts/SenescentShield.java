package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactBlockProjectiles;

public class SenescentShield extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float knockbackProj = 10.0f;

	public SenescentShield() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new ContactBlockProjectiles(state, hbox, b, knockbackProj));
			}
		});
		
		return enchantment;
	}
}
