package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.HomingMouse;
import com.mygdx.hadal.utils.Stats;

public class KinesisLens extends Artifact {

	private static final int slotCost = 3;
	
	private static final float projSpdReduction = -0.4f;
	private static final float bonusProjLifespan = 0.4f;
	private static final float homePower = 120.0f;

	public KinesisLens() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, projSpdReduction, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLifespan, p),
				new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }
				
				hbox.addStrategy(new HomingMouse(state, hbox, p, homePower));
				hbox.setGravity(0.0f);
			}
		});
	}
}
