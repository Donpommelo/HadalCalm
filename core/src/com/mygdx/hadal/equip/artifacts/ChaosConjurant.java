package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PROC;

public class ChaosConjurant extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float PROC_CD = 5.0f;
	
	private static final float BASE_DAMAGE = 28.0f;

	private static final int METEOR_NUMBER = 5;
	private static final float METEOR_INTERVAL = 0.2f;
	private static final float SPREAD = 15.0f;
	
	public ChaosConjurant() {
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

					WeaponUtils.createMeteors(state, p.getPlayer(), p.getPlayer().getPosition(), METEOR_NUMBER, METEOR_INTERVAL,
							BASE_DAMAGE, SPREAD);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf(METEOR_NUMBER),
				String.valueOf((int) BASE_DAMAGE)};
	}
}
