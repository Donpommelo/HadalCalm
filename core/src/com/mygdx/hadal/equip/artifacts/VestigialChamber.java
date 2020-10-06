package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;

public class VestigialChamber extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float procCd = 2.0f;
	
	private static final float reticleSize = 80.0f;
	private static final float reticleLifespan = 0.75f;
	private static final int explosionRadius = 100;
	private static final float explosionDamage = 18.0f;
	private static final float explosionKnockback = 20.0f;
	
	public VestigialChamber() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {
			
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
					hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
						
						@Override
						public void die() {
							WeaponUtils.createExplodingReticle(state, hbox.getPixelPosition(), inflicted.getSchmuck(), reticleSize, reticleLifespan, explosionDamage, explosionKnockback, explosionRadius);
						}
					});
				}
			}
		};
		return enchantment;
	}
}
