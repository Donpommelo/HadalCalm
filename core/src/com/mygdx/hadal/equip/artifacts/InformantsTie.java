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

	private final static String name = "Informant's Tie";
	private final static String descr = "Homing Projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public InformantsTie() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, -0.5f, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURA, 0.5f, b),
				new Status(state, name, descr, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new HitboxHomingStrategy(state, hbox, b, inflicted.getSchmuck().getHitboxfilter()));
			}
		});
		
		return enchantment;
	}
}
