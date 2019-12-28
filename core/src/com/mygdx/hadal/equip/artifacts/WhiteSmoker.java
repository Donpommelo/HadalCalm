package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitStatusStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.statuses.Status;

public class WhiteSmoker extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float fireDuration = 4.0f;
	private final static float fireDamage = 3.0f;
	private final static float procCd = .50f;
	
	public WhiteSmoker() {
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
					hbox.addStrategy(new HitboxOnContactUnitStatusStrategy(state, hbox, inflicted, new Ablaze(state, fireDuration, inflicted, inflicted, fireDamage)));
					new ParticleEntity(state, hbox, Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
				}
			}
		};
		return enchantment;
	}
}
