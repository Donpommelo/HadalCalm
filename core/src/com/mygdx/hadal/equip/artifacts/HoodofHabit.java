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

	private static final int SLOT_COST = 1;
	
	private static final float HP_THRESHOLD = 0.5f;
	private static final float INVIS_DURATION = 10.0f;
	private static final float PROC_CD = 1.0f;
	
	public HoodofHabit() {
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
				if (procCdCount >= PROC_CD) {
					if (p.getCurrentHp() > HP_THRESHOLD * p.getStat(Stats.MAX_HP) &&
						p.getCurrentHp() - damage <= HP_THRESHOLD * p.getStat(Stats.MAX_HP)) {
						procCdCount = 0;

						SyncedAttack.INVISIBILITY_ON.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(),
								true, INVIS_DURATION);
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (HP_THRESHOLD * 100)),
				String.valueOf((int) INVIS_DURATION)};
	}
}
