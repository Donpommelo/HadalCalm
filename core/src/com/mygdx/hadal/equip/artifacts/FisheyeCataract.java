package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class FisheyeCataract extends Artifact {

	private static final int slotCost = 1;

	private static final float visionReduction = -0.5f;
	private static final float minDistance = 200.0f;
	private static final float maxDistance = 1200.0f;
	private static final float minDamageReduction = 0.25f;
	private static final float maxDamageReduction = 0.75f;

	public FisheyeCataract() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.VISION_RADIUS, visionReduction, p) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (perp != null) {
					float dstSquared = perp.getSchmuck().getPosition().dst2(p.getSchmuck().getPosition());
					if (dstSquared >= minDistance) {
						float diff = (Math.min(dstSquared, maxDistance) - minDistance) / (maxDistance - minDistance);
						return damage * (1.0f - (diff * (maxDamageReduction - minDamageReduction) + minDamageReduction));
					}
				}
				return damage;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(visionReduction * 100)),
				String.valueOf((int) (minDamageReduction * 100)),
				String.valueOf((int) (maxDamageReduction * 100))};
	}
}
