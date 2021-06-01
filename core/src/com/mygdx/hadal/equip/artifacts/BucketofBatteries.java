package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactUnitShock;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class BucketofBatteries extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;
	
	private static final float baseDamage = 11.0f;
	private static final int radius = 25;
	private static final int chainAmount = 3;
	
	private static final float procCd = 0.5f;
	
	public BucketofBatteries() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {

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
					
					hbox.addStrategy(new ContactUnitShock(state, hbox, inflicted, baseDamage, radius, chainAmount, inflicted.getSchmuck().getHitboxfilter()));
					hbox.addStrategy(new CreateParticles(state, hbox, inflicted, Particle.LIGHTNING, hbox.getLifeSpan(), 1.0f).setParticleSize(90));
				}
			}
		});
		
		return enchantment;
	}
}
