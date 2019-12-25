package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxPoisonTrailStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ShillersBasidia extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private static final int poisonRadius = 100;
	private static final float poisonDamage = 15 / 60f;
	private static float poisonDuration = 2.0f;
	
	public ShillersBasidia() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			private float procCd = .50f;
			
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
					hbox.addStrategy(new HitboxPoisonTrailStrategy(state, hbox, b, poisonRadius, poisonDamage, poisonDuration, b.getSchmuck().getHitboxfilter()));
				}
			}
		};
		return enchantment;
	}
}
