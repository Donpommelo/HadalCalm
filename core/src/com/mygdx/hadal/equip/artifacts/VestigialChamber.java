package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;

public class VestigialChamber extends Artifact {

	private static final int slotCost = 2;
	
	private static final float procCd = 1.5f;
	
	private static final float reticleSize = 80.0f;
	private static final float reticleLifespan = 0.75f;
	private static final int explosionRadius = 100;
	private static final float explosionDamage = 32.0f;
	private static final float explosionKnockback = 20.0f;
	
	public VestigialChamber() {
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
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
						
						@Override
						public void die() {
							WeaponUtils.createExplodingReticle(state, hbox.getPixelPosition(), p.getSchmuck(), reticleSize,
									reticleLifespan, explosionDamage, explosionKnockback, explosionRadius, DamageSource.VESTIGIAL_CHAMBER);
						}
					});
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(reticleLifespan),
				String.valueOf((int) explosionDamage)};
	}
}
