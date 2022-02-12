package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_SCALE;

public class MaskofSympathy extends Artifact {

	private static final int slotCost = 1;
	
	private static final float amount = 0.5f;
	
	public MaskofSympathy() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (!perp.equals(p) && damage > 0 && perp instanceof PlayerBodyData playerData) {
					playerData.fuelSpend(damage * amount);
				}
				return damage;
			}
		}.setPriority(PRIORITY_SCALE);
	}
}
