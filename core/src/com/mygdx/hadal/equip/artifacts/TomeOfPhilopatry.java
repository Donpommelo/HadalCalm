package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ReturnToUser;
import com.mygdx.hadal.utils.Stats;

public class TomeOfPhilopatry extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusProjLifespan = 0.5f;
	private static final float bonusProjDurability = 1.0f;
	private static final float returnAmp = 4.0f;
	
	public TomeOfPhilopatry() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLifespan, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, bonusProjDurability, p),
				new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsMovement()) {
					hbox.addStrategy(new ReturnToUser(state, hbox, p, hbox.getStartVelo().len() * returnAmp));
					hbox.setGravity(0.0f);
				}
			}
		});
	}
}
