package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PRIORITY_PROC;

public class GomezsAmygdala extends Artifact {

	private static final int slotCost = 2;
	
	private static final float spdBuff = 0.5f;
	private static final float damageBuff = 0.4f;
	private static final float buffDuration = 3.0f;

	public GomezsAmygdala() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = buffDuration;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < buffDuration) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= buffDuration && damage > 0) {
					procCdCount -= buffDuration;
					
					SoundEffect.MAGIC18_BUFF.playUniversal(state, p.getPlayer().getPixelPosition(), 0.5f, false);

					new ParticleEntity(state, p.getPlayer(), Particle.PICKUP_ENERGY, 1.0f, buffDuration, true, SyncType.CREATESYNC);
					p.getPlayer().setShader(Shader.PULSE_RED, buffDuration, true);
					
					p.addStatus(new StatusComposite(state, buffDuration, false, perp, p,
							new StatChangeStatus(state, Stats.GROUND_SPD, spdBuff, p),
							new StatChangeStatus(state, Stats.DAMAGE_AMP, damageBuff, p)));
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) buffDuration),
				String.valueOf((int) (spdBuff * 100)),
				String.valueOf((int) (damageBuff * 100))};
	}
}
