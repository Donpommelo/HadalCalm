package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactUnitShock;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class BucketofBatteries extends Artifact {

	private static final int slotCost = 3;
	
	private static final float baseDamage = 11.0f;
	private static final int radius = 25;
	private static final int chainAmount = 4;
	
	private static final float procCd = 0.5f;
	
	public BucketofBatteries() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {

			private float procCdCount = procCd;
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

					if (hbox.isEffectsHit()) {
						hbox.addStrategy(new ContactUnitShock(state, hbox, p, baseDamage, radius, chainAmount,
								p.getSchmuck().getHitboxFilter(), SyncedAttack.SHOCK_BUCKET));
					}
					if (hbox.isEffectsVisual()) {
						hbox.addStrategy(new CreateParticles(state, hbox, p, Particle.LIGHTNING, hbox.getLifeSpan(), 1.0f)
								.setParticleSize(60).setSyncType(SyncType.NOSYNC));
					}
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(procCd),
				String.valueOf((int) baseDamage),
				String.valueOf(chainAmount)};
	}
}
