package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactUnitChainLightning;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class BucketofBatteries extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private final static float baseDamage = 5.0f;
	private final static int chainAmount = 4;
	
	private final static float procCd = 0.5f;
	
	public BucketofBatteries() {
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
					procCdCount -= procCd;
					
					hbox.addStrategy(new ContactUnitChainLightning(state, hbox, inflicted, chainAmount, baseDamage));
					hbox.addStrategy(new CreateParticles(state, hbox, inflicted, Particle.LIGHTNING, hbox.getLifeSpan(), 3.0f, 30));
				}
			}
		});
		
		return enchantment;
	}
}
