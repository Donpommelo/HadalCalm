package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AnarchistsCookbook extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float baseDamage = 0.0f;
	private static final float knockback = 0.0f;
	private static final Vector2 projectileSize = new Vector2(20, 20);
	private static final float lifespan = 3.0f;
		
	private static final int explosionRadius = 150;
	private static final float explosionDamage = 40.0f;
	private static final float explosionKnockback = 25.0f;
	
	private static final float procCd = 1.0f;
	
	public AnarchistsCookbook() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					SoundEffect.LAUNCHER.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.2f, false);

					WeaponUtils.createGrenade(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, inflicted.getSchmuck(), 
							baseDamage, knockback, lifespan, new Vector2(0, 0), false, explosionRadius, explosionDamage, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		};
		
		return enchantment;
	}
}
