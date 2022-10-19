package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class ChaosConjurant extends Artifact {

	private static final int slotCost = 2;

	private static final float procCd = 5.0f;
	
	private static final float baseDamage = 28.0f;

	private static final float meteorDuration = 1.0f;
	private static final float meteorInterval = 0.2f;
	private static final float spread = 10.0f;
	
	public ChaosConjurant() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					new ParticleEntity(state, p.getSchmuck(), Particle.RING, 1.0f, meteorDuration, true,
							SyncType.CREATESYNC).setScale(0.4f);
					WeaponUtils.createMeteors(state, p.getSchmuck().getPosition(), p.getSchmuck(), meteorDuration,
							meteorInterval, spread, baseDamage, DamageSource.CHAOS_CONJURANT);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) procCd),
				String.valueOf((int) (meteorDuration / meteorInterval)),
				String.valueOf((int) baseDamage)};
	}
}
