package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactBlockProjectilesStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class SenescentShield extends Artifact {

	private final static String name = "Senescent Shield";
	private final static String descr = "Deflective Projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float knockbackProj = 100.0f;

	public SenescentShield() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new Status(state, name, descr, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new HitboxOnContactBlockProjectilesStrategy(state, hbox, b, b.getCurrentTool(), knockbackProj));
			}
		});
		
		return enchantment;
	}
}
