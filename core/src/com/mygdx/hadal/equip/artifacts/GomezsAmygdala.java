package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class GomezsAmygdala extends Artifact {

	private final static String name = "Gomez's Amygdala";
	private final static String descr = "Temporarily boosts speed and damage when taking damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final float dura = 2.0f;
	
	public GomezsAmygdala() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = 2.0f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;

				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount -= procCd;
					
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f, procCd, true, particleSyncType.TICKSYNC);
					
					inflicted.addStatus(new StatusComposite(state, dura, "Self-Preservatory", "Bonus Stats", false, perp, inflicted,
							new StatChangeStatus(state, Stats.GROUND_SPD, 0.5f, inflicted),
							new StatChangeStatus(state, Stats.DAMAGE_AMP, 0.3f, inflicted)
							));
				}
				return damage;
			}
		};
		return enchantment;
	}
}
