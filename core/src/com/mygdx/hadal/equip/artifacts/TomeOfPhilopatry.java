package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ReturnToUser;
import com.mygdx.hadal.constants.Stats;

public class TomeOfPhilopatry extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_PROJ_LIFESPAN = 0.75f;
	private static final float BONUS_PROJ_DURABILITY = 1.0f;
	private static final float RETURN_AMP = 4.0f;
	
	public TomeOfPhilopatry() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, BONUS_PROJ_LIFESPAN, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, BONUS_PROJ_DURABILITY, p),
				new Status(state, p) {

			@Override
			public void onHitboxInit(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }
				hbox.setSynced(true);
				hbox.setSyncedDelete(true);
			}

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsMovement()) {
					hbox.addStrategy(new ReturnToUser(state, hbox, p, hbox.getStartVelo().len() * RETURN_AMP));
					hbox.setGravity(0.0f);
				}
			}
		});
	}
}
