package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class Ice9 extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float SLOW_DURA = 1.0f;
	private static final float SLOW = 0.25f;
	
	public Ice9() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsHit()) {
					hbox.addStrategy(new ContactUnitSlow(state, hbox, p, SLOW_DURA, SLOW, Particle.ICE_CLOUD));
				}
				if (hbox.isEffectsVisual()) {
					hbox.addStrategy(new CreateParticles(state, hbox, p, Particle.ICE_CLOUD)
							.setParticleSize(50));
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) SLOW_DURA),
				String.valueOf((int) (SLOW * 100))};
	}
}
