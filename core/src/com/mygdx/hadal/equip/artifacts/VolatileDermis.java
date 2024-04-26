package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.artifact.VolatileDermisActivate;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PROC;

public class VolatileDermis extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float PROC_CD = 4.0f;
	private static final float CHAIN_DAMAGE = VolatileDermisActivate.CHAIN_DAMAGE;
	private static final int CHAIN_AMOUNT = VolatileDermisActivate.CHAIN_AMOUNT;

	public VolatileDermis() { super(SLOT_COST); }

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
					SyncedAttack.VOLATILE_DERMIS.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(), true);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf((int) CHAIN_DAMAGE),
				String.valueOf(CHAIN_AMOUNT)};
	}
}
