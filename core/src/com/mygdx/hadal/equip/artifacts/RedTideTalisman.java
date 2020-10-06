package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class RedTideTalisman extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float fireDuration = 5.0f;
	private static final float fireDamage = 3.0f;
	
	public RedTideTalisman() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsHit()) {
					hbox.addStrategy(new ContactUnitBurn(state, hbox, inflicted, fireDuration, fireDamage));
				}
				if (hbox.isEffectsVisual()) {
					hbox.addStrategy(new CreateParticles(state, hbox, inflicted, Particle.FIRE, hbox.getLifeSpan(), 1.0f).setParticleSize(50));
				}
			}
		};
		return enchantment;
	}
}
