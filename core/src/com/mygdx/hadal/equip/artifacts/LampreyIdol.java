package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
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
	private static final float DAMAGE = 2.5f;
	private static final float HP_THRESHOLD = 0.5f;
	
	public LampreyIdol() {
		super(SLOT_COST);
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
					
					if ((p.getCurrentHp() / p.getStat(Stats.MAX_HP)) >= HP_THRESHOLD) {
						p.receiveDamage(DAMAGE, new Vector2(), p, true, null, DamageSource.LAMPREY_IDOL);
					}
				}
				procCdCount += delta;
			}
			
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
				String.valueOf((int) (LIFESTEAL_ENEMY * 100)),
				String.valueOf((int) (HP_THRESHOLD * 100)),
				String.valueOf(DAMAGE)};
	}
}
