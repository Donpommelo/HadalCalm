package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxHomingStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class InformantsTie extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	public InformantsTie() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, -0.75f, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, 0.75f, b),
				new Status(state, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new HitboxHomingStrategy(state, hbox, b, inflicted.getSchmuck().getHitboxfilter()));
				hbox.setGravity(0.0f);
			}
		});
		
		return enchantment;
	}
}
