package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DiatomaceousEarth extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;

	private static final float durationMultiplier = 0.25f;
	private static final float damageResistance = 0.6f;
	private static final float knockbackResistance = 0.9f;

	public DiatomaceousEarth() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void afterActiveItem(ActiveItem tool) {
				SoundEffect.MAGIC1_ACTIVE.playUniversal(inflicted.getSchmuck().getState(), inflicted.getSchmuck().getPixelPosition(), 0.4f, false);

				float dura = tool.getMaxCharge() * durationMultiplier;

				new ParticleEntity(state, inflicted.getSchmuck(), Particle.RING, 1.0f, dura, true, ParticleEntity.particleSyncType.CREATESYNC).setScale(0.4f);

				inflicted.addStatus(new StatusComposite(state, dura, false, inflicted, inflicted,
					new StatChangeStatus(state, Stats.DAMAGE_RES, damageResistance, inflicted),
					new StatChangeStatus(state, Stats.KNOCKBACK_RES, knockbackResistance, inflicted)));
			}
		};
		return enchantment;
	}
}
