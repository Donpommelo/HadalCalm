package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class Number1BossMug extends Artifact {

	private static final int slotCost = 1;
	
	private static final float procCd = 10.0f;

	private static final float fieldSize = 280.0f;
	private static final float fieldHeal = 0.2f;
	private static final float healDuration = 5.0f;

	public Number1BossMug() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

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
					procCdCount = 0;

					SyncedAttack.HEALING_FIELD.initiateSyncedAttackNoHbox(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(), true,
							fieldSize, fieldHeal, healDuration);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) procCd),
				String.valueOf((int) healDuration),
				String.valueOf((int) (fieldHeal * 60))};
	}
}
