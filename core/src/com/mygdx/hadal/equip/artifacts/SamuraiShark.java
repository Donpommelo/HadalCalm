package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class SamuraiShark extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final float critChance = 0.2f;
	private final float critDamageBoost = 40.0f;
	private final float critSpeedMultiplier = 3.0f;
	private static float procCd = 5.0f;
	
	public SamuraiShark() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
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
					if (GameStateManager.generator.nextDouble() < critChance) {
						procCdCount -= procCd;
						hbox.setStartVelo(hbox.getStartVelo().scl(critSpeedMultiplier));
						hbox.addStrategy(new DamageStandard(state, hbox, b, critDamageBoost, 0, DamageTypes.RANGED));
						hbox.addStrategy(new DieParticles(state, hbox, b, Particle.EXPLOSION));
					}
				}
			}
		});
		
		return enchantment;
	}
}
