package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;

public class Ice9 extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private static final float procCd = 2.0f;
	
	private static final float slowDura = 2.0f;
	private static final float slow = 0.8f;
	
	public Ice9() {
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
					hbox.addStrategy(new ContactUnitSlow(state, hbox, inflicted, slowDura, slow));
				}
			}
		};
		return enchantment;
	}
}
