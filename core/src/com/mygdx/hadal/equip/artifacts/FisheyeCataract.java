package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class FisheyeCataract extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float VISION_REDUCTION = -0.5f;
	private static final float MIN_DISTANCE = 200.0f;
	private static final float MAX_DISTANCE = 1200.0f;
	private static final float MIN_DAMAGE_REDUCTION = 0.25f;
	private static final float MAX_DAMAGE_REDUCTION = 0.75f;

	public FisheyeCataract() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.VISION_RADIUS, VISION_REDUCTION, p) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (perp != null) {
					float dstSquared = perp.getSchmuck().getPosition().dst2(p.getSchmuck().getPosition());
					if (dstSquared >= MIN_DISTANCE) {
						float diff = (Math.min(dstSquared, MAX_DISTANCE) - MIN_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE);
						return damage * (1.0f - (diff * (MAX_DAMAGE_REDUCTION - MIN_DAMAGE_REDUCTION) + MIN_DAMAGE_REDUCTION));
					}
				}
				return damage;
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(VISION_REDUCTION * 100)),
				String.valueOf((int) (MIN_DAMAGE_REDUCTION * 100)),
				String.valueOf((int) (MAX_DAMAGE_REDUCTION * 100))};
	}
}
