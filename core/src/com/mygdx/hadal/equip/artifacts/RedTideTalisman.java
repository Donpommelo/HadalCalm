package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitStatus;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class RedTideTalisman extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float fireDuration = 4.0f;
	private final static float fireDamage = 3.0f;
	private final static float procCd = 0.5f;
	
	public RedTideTalisman() {
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
					hbox.addStrategy(new ContactUnitStatus(state, hbox, inflicted, new Ablaze(state, fireDuration, inflicted, inflicted, fireDamage)));
					hbox.addStrategy(new CreateParticles(state, hbox, inflicted, Particle.FIRE, 3.0f));
				}
			}
		};
		return enchantment;
	}
}
