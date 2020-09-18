package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class SamuraiShark extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final float critChance = 0.15f;
	private final float critDamageBoost = 1.5f;
	private final float critSpeedMultiplier = 2.0f;
	
	public SamuraiShark() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				
				if (!hbox.isEffectsHit()) { return; } 
				
				if (GameStateManager.generator.nextDouble() < critChance) {
					hbox.setStartVelo(hbox.getStartVelo().scl(critSpeedMultiplier));
					hbox.addStrategy(new DieParticles(state, hbox, b, Particle.EXPLOSION));
					hbox.setDamageMultiplier(critDamageBoost);
				}
			}
		});
		
		return enchantment;
	}
}
