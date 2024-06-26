package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Regeneration;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PROC;

public class Blastema extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float REGEN_CD = 4.0f;
	private static final float REGEN = 0.025f;
	
	public Blastema() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private final float procCd = REGEN_CD;
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount -= procCd;
					p.addStatus(new Regeneration(state, REGEN_CD, p, p, REGEN * inflicted.getStat(Stats.MAX_HP)));
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) REGEN_CD),
				String.valueOf(REGEN * 100)};
	}
}
