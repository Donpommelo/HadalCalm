package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AnarchistsCookbook extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float baseDamage = 15.0f;
	private final static float knockback = 0.0f;
	private final static Vector2 projectileSize = new Vector2(20, 20);
	private final static float lifespan = 3.0f;
		
	private final static int explosionRadius = 150;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 25.0f;
	
	private final static float procCd = 1.0f;
	
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
					WeaponUtils.createGrenade(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, inflicted.getSchmuck(), 
							baseDamage, knockback, lifespan, new Vector2(0, 0), false, explosionRadius, explosionDamage, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		};
		
		return enchantment;
	}
}
