package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DiatomaceousEarth extends Artifact {

	private static final int slotCost = 2;

	private static final float durationMultiplier = 0.5f;
	private static final float damageResistance = 0.75f;
	private static final float knockbackResistance = 0.9f;

	public DiatomaceousEarth() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void afterActiveItem(ActiveItem tool) {
				SoundEffect.MAGIC1_ACTIVE.playUniversal(p.getSchmuck().getState(), p.getSchmuck().getPixelPosition(), 0.4f, false);

				float dura = tool.getMaxCharge() * durationMultiplier;
				new ParticleEntity(state, p.getSchmuck(), Particle.RING, 1.0f, dura, true, ParticleEntity.particleSyncType.CREATESYNC).setScale(0.4f);

				p.addStatus(new StatusComposite(state, dura, false, p, p,
					new StatChangeStatus(state, Stats.DAMAGE_RES, damageResistance, p),
					new StatChangeStatus(state, Stats.KNOCKBACK_RES, knockbackResistance, p)));
			}
		};
	}
}
