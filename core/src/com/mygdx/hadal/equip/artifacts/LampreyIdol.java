package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class LampreyIdol extends Artifact {

	private static final int slotCost = 2;
	
	private static final float lifestealPlayer = 0.1f;
	private static final float lifestealEnemy = 0.02f;
	private static final float damage = 2.5f;
	private static final float hpThreshold = 0.5f;
	
	public LampreyIdol() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			private float procCdCount;
			private static final float procCd = 1.0f;
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					if ((p.getCurrentHp() / p.getStat(Stats.MAX_HP)) >= hpThreshold) {
						p.receiveDamage(damage, new Vector2(), p, true, null);
					}
				}
				procCdCount += delta;
			}
			
			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageTypes... tags) {
				if (vic instanceof PlayerBodyData) {
					p.regainHp(lifestealPlayer * damage, p, true, DamageTypes.LIFESTEAL);
				} else {
					p.regainHp(lifestealEnemy * damage, p, true, DamageTypes.LIFESTEAL);
				}
				return damage;
			}
		});
	}
}
