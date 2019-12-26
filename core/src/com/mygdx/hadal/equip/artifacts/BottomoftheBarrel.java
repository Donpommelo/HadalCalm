package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class BottomoftheBarrel extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float damageAmp = 2.0f;
	private final static float ammoThreshold = 0.25f;
	
	public BottomoftheBarrel() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				
				if (inflicted.getCurrentTool() instanceof RangedWeapon) {
					if (((RangedWeapon)inflicted.getCurrentTool()).getAmmoPercent() <= ammoThreshold) {
						return damage * damageAmp;
					}
				}
				return damage;
			}
			
		});
		return enchantment;
	}
}
