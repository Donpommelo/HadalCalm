package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ReturnToUser;
import com.mygdx.hadal.utils.Stats;

public class TomeOfPhilopatry extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float bonusProjLifespan = 0.5f;
	private final static float bonusProjDurability = 1.0f;
	private final static float returnAmp = 1.5f;
	
	public TomeOfPhilopatry() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLifespan, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, bonusProjDurability, b),
				new Status(state, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new ReturnToUser(state, hbox, b, hbox.getStartVelo().len() * returnAmp));
				hbox.setGravity(0.0f);
			}
		});
		
		return enchantment;
	}
}