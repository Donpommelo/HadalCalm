package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class BookofBurial extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;

	private static final Vector2 projectileSize = new Vector2(75, 30);
	private static final float mineLifespan = 24.0f;

	private static final float projectileSpeed = 60.0f;

	private static final int explosionRadius = 250;
	private static final float explosionDamage = 50.0f;
	private static final float explosionKnockback = 50.0f;

	private static final float primeDelay = 2.0f;

	private static final float procCd = 7.5f;

	public BookofBurial() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			private final Vector2 angle = new Vector2(1, 0);
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;

					WeaponUtils.createProximityMine(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), projectileSpeed, projectileSize,
						primeDelay, mineLifespan, explosionDamage, explosionKnockback, explosionRadius);
				}
				
				return damage;
			}
		};
		return enchantment;
	}
}
