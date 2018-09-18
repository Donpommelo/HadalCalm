package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactChainStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class BucketofBatteries extends Artifact {

	private final static String name = "Bucket of Batteries";
	private final static String descr = "Projectiles chain";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public BucketofBatteries() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 34, 3.0f, b),
				new Status(state, name, descr, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new HitboxOnContactChainStrategy(state, hbox, b, 5, inflicted.getSchmuck().getHitboxfilter()));
			}
		});
		
		return enchantment;
	}
}
