package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class SeafoamTalisman extends Artifact {

	private final static String name = "Seafoam Talisman";
	private final static String descr = "+Ranged Attack Speed, Antigravity Projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public SeafoamTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 27, 0.20f, b),
				new Status(state, name, descr, b) {

					@Override
					public void onHitboxCreation(Hitbox hbox) {
						hbox.setGrav(-2.0f);
					}
			}
		);
		return enchantment;
	}
}
