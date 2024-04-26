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

public class GomezsAmygdala extends Artifact {

	private static final int slotCost = 2;
	
	private static final float spdBuff = 0.5f;
	private static final float damageBuff = 0.4f;
	private static final float buffDuration = 3.0f;

	public GomezsAmygdala() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = buffDuration;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < buffDuration) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= buffDuration && damage > 0) {
					procCdCount -= buffDuration;
					SyncedAttack.GOMEZS_AMYGDALA.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) buffDuration),
				String.valueOf((int) (spdBuff * 100)),
				String.valueOf((int) (damageBuff * 100))};
	}
}
