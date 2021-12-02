package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class RedTideTalisman extends Artifact {

	private static final int slotCost = 1;
	
	private static final float fireDuration = 4.0f;
	private static final float fireDamage = 3.0f;
	
	public RedTideTalisman() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsHit()) {
					hbox.addStrategy(new ContactUnitBurn(state, hbox, p, fireDuration, fireDamage));
				}
				if (hbox.isEffectsVisual()) {
					hbox.addStrategy(new CreateParticles(state, hbox, p, Particle.FIRE, hbox.getLifeSpan(), 1.0f).setParticleSize(50));
				}
			}
		};
	}
}
