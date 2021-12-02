package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DeplorableApparatus extends Artifact {

	private static final int slotCost = 2;
	
	private static final float hpReduction = -0.4f;
	private static final float hpRegen = 13.0f;
	private static final float procCd = 1.0f;
	
	public DeplorableApparatus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_HP_PERCENT, hpReduction, p),
				new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				
				if (procCdCount >= procCd) {
					p.regainHp(hpRegen * delta, p, true, DamageTypes.REGEN);
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (damage > 0) {
					procCdCount = 0;
				}
				return damage;
			}
		});
	}
}
