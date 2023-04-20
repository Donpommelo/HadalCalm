package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_MULT_SCALE;

public class FracturePlate extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float CD = 4.0f;
	private float procCdCount = 0;

	private static final float MAX_SHIELD = 0.2f;
	private float shield;
	
	public FracturePlate() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= 0) {
					procCdCount -= delta;
				}
				
				if (procCdCount < 0 && shield != MAX_SHIELD * p.getStat(Stats.MAX_HP)) {
					shield = MAX_SHIELD * p.getStat(Stats.MAX_HP);
					SyncedAttack.FRACTURE_PLATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(), true, 1.0f);
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				float finalDamage = damage;
				if (damage > 0 && shield > 0) {
					procCdCount = CD;
					if (shield > damage) {
						shield -= damage;
						finalDamage = 0;
					} else {
						finalDamage = damage - shield;
						shield = 0;
					}
					SyncedAttack.FRACTURE_PLATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(), true, 0.0f);
				}
				return finalDamage;
			}
		}.setPriority(PRIORITY_MULT_SCALE).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) CD),
				String.valueOf((int) (MAX_SHIELD * 100))};
	}
}
