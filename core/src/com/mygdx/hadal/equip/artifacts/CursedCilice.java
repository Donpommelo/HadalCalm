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

import static com.mygdx.hadal.constants.Constants.PRIORITY_SCALE;

public class CursedCilice extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float AMOUNT = 1.5f;
	private static final float PROC_CD = 0.5f;

	public CursedCilice() {	super(SLOT_COST); }

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
				if (damage > 0) {
					p.fuelGain(damage * AMOUNT);
					if (procCdCount >= PROC_CD) {
						procCdCount = 0;
						SyncedAttack.ARTIFACT_FUEL_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_SCALE).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (AMOUNT * 100))};
	}
}
