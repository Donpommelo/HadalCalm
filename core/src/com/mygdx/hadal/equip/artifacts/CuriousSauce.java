package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxRemoveStrategyStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CuriousSauce extends Artifact {

	private final static String name = "Curious Sauce";
	private final static String descr = "Bouncy Projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public CuriousSauce() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_RESTITUTION, 1.0f, b),
				new Status(state, name, descr, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new HitboxRemoveStrategyStrategy(state, hbox, b, HitboxOnContactWallDieStrategy.class));
				hbox.setSensor(false);
			}
		});
		
		return enchantment;
	}
}
