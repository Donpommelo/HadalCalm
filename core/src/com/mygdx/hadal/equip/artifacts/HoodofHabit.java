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

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class HoodofHabit extends Artifact {

	private static final int slotCost = 1;
	
	private static final float hpThreshold = 0.5f;
	private static final float invisDuration = 10.0f;
	private static final float procCd = 1.0f;
	
	public HoodofHabit() {
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
				if (procCdCount >= procCd) {
					if (p.getCurrentHp() > hpThreshold * p.getStat(Stats.MAX_HP) &&
						p.getCurrentHp() - damage <= hpThreshold * p.getStat(Stats.MAX_HP)) {
						procCdCount = 0;

						SyncedAttack.INVISIBILITY_ON.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(),
								true, invisDuration);
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (hpThreshold * 100)),
				String.valueOf((int) invisDuration)};
	}
}
