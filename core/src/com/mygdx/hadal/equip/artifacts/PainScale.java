package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_SCALE;

public class PainScale extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float AMOUNT = 0.2f;
	
	public PainScale() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damage > 0) {
					p.getPlayer().getMagicHelper().getMagic().gainCharge(damage * AMOUNT);
				}
				return damage;
			}
		}.setPriority(PRIORITY_SCALE);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (1.0f / AMOUNT))};
	}
}
