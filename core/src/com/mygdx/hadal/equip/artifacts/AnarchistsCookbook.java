package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AnarchistsCookbook extends Artifact {

	private static final int slotCost = 1;
	
	private static final float baseDamage = 0.0f;
	private static final float knockback = 0.0f;
	private static final Vector2 spriteSize = new Vector2(60, 141);
	private static final Vector2 projectileSize = new Vector2(60, 60);
	private static final float lifespan = 3.0f;
		
	private static final int explosionRadius = 150;
	private static final float explosionDamage = 40.0f;
	private static final float explosionKnockback = 25.0f;
	
	private static final float procCd = 3.0f;
	
	public AnarchistsCookbook() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment= new Status(state, p) {
			
			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					SoundEffect.LAUNCHER.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.2f, false);

					WeaponUtils.createBomb(state, p.getSchmuck().getPixelPosition(), spriteSize, projectileSize, p.getSchmuck(),
							baseDamage, knockback, lifespan, new Vector2(), false, explosionRadius, explosionDamage, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		};
	}
}
