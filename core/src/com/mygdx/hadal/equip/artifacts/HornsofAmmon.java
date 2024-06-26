package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PROC;

public class HornsofAmmon extends Artifact {

	private static final int SLOT_COST = 3;
	
	private static final float DURATION = 1.0f;
	private static final float PROC_CD = 5.0f;

	public HornsofAmmon() {
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

					p.receiveDamage(damage, new Vector2(), perp, false, damaging, source, tags);

					SyncedAttack.INVINCIBILITY.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(),
							true, DURATION);
					return 0;
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) DURATION),
				String.valueOf((int) PROC_CD)};
	}
}
