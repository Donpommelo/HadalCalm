package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PROC;

public class Number1BossMug extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float PROC_CD = 10.0f;

	private static final float FIELD_SIZE = 280.0f;
	private static final float FIELD_HEAL = 0.2f;
	private static final float HEAL_DURATION = 5.0f;

	public Number1BossMug() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
			}

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {

				if (procCdCount >= PROC_CD && damage > 0) {
					procCdCount = 0;

					SyncedAttack.HEALING_FIELD.initiateSyncedAttackNoHbox(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(), true,
							FIELD_SIZE, FIELD_HEAL, HEAL_DURATION);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf((int) HEAL_DURATION),
				String.valueOf((int) (FIELD_HEAL * 60))};
	}
}
