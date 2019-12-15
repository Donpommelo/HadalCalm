package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxPoisonTrailStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ShillersBasidia extends Artifact {

	private final static String name = "Shiller's Basidia";
	private final static String descr = "Projectile Poison trail.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public ShillersBasidia() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = .20f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					hbox.addStrategy(new HitboxPoisonTrailStrategy(state, hbox, b, 60, 20 / 60f, 2.0f, b.getSchmuck().getHitboxfilter()));
				}
			}
		};
		return enchantment;
	}
}
