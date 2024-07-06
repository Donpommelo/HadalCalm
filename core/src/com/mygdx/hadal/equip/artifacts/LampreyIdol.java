package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_SCALE;

public class LampreyIdol extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float LIFESTEAL_PLAYER = 0.1f;
	private static final float LIFESTEAL_ENEMY = 0.02f;

	public LampreyIdol() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (vic instanceof PlayerBodyData) {
					p.regainHp(LIFESTEAL_PLAYER * damage, p, true, DamageTag.LIFESTEAL);
				} else {
					p.regainHp(LIFESTEAL_ENEMY * damage, p, true, DamageTag.LIFESTEAL);
				}
				return damage;
			}
		}).setPriority(PRIORITY_SCALE);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (LIFESTEAL_PLAYER * 100)),
				String.valueOf((int) (LIFESTEAL_ENEMY * 100))};
	}
}
