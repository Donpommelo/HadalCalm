package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SimpleMind extends Artifact {

	private final static String name = "Simple Mind";
	private final static String descr = "Linear Projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public SimpleMind() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_CLIP, 0.5f, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, 0.5f, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, 0.5f, b),
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, 0.5f, b),
				new Status(state, name, descr, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.setGrav(0.0f);
				if (hbox.getStartVelo().x > 0) {
					hbox.setStartVelo(hbox.getStartVelo().setAngle(0));
				} else {
					hbox.setStartVelo(hbox.getStartVelo().setAngle(180));
				}
			}
		});
		
		return enchantment;
	}
}
