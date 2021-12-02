package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class SamuraiShark extends Artifact {

	private static final int slotCost = 1;
	
	private final float critChance = 0.15f;
	private final float critDamageBoost = 1.75f;
	private final float critSpeedMultiplier = 2.0f;
	
	public SamuraiShark() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }
				
				if (MathUtils.randomBoolean(critChance)) {
					hbox.setStartVelo(hbox.getStartVelo().scl(critSpeedMultiplier));
					hbox.addStrategy(new DieParticles(state, hbox, p, Particle.EXPLOSION));
					hbox.setDamageMultiplier(critDamageBoost);
				}
			}
		});
	}
}
