package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.PoisonTrail;

public class ShillersDeathcap extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private static final int poisonRadius = 100;
	private static final float poisonDamage = 10 / 60f;
	private static float poisonDuration = 1.0f;
	private static final float procCd = 1.0f;
	
	public ShillersDeathcap() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
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
					hbox.addStrategy(new PoisonTrail(state, hbox, b, poisonRadius, poisonDamage, poisonDuration, b.getSchmuck().getHitboxfilter()));
				}
			}
		};
		return enchantment;
	}
}
