package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.HomingUnit;
import com.mygdx.hadal.constants.Stats;

public class InformantsTie extends Artifact {

	private static final int SLOT_COST = 3;

	private static final float PROJ_SPD_REDUCTION = -0.25f;
	private static final float BONUS_PROJ_LIFESPAN = 0.4f;
	private static final float HOME_POWER = 0.25f;
	private static final int HOME_RADIUS = 50;

	public InformantsTie() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, PROJ_SPD_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, BONUS_PROJ_LIFESPAN, p),
				new Status(state, p) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }
				
				hbox.addStrategy(new HomingUnit(state, hbox, p, hbox.getStartVelo().len2() * HOME_POWER, HOME_RADIUS));
				hbox.setGravity(0.0f);
			}
		});
	}
}
