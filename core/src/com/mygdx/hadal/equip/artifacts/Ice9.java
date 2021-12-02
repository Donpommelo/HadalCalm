package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;

public class Ice9 extends Artifact {

	private static final int slotCost = 2;
	private static final float procCd = 2.0f;
	private static final float slowDura = 2.0f;
	private static final float slow = 0.8f;
	
	public Ice9() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					hbox.addStrategy(new ContactUnitSlow(state, hbox, p, slowDura, slow, Particle.ICE_CLOUD));
				}
			}
		};
	}
}
