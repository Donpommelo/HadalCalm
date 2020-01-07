package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class OlFaithful extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static int bonusSlots = -2;
	private static final float bonusAtkSpd = 0.3f;
	private static final float bonusReloadSpd = 0.3f;
	private final static float bonusAmmo = 1.5f;
	
	public OlFaithful() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.WEAPON_SLOTS, bonusSlots, b) {
			
				@Override
				public void onRemove(Status s) {
					System.out.println(((PlayerBodyData)inflicted).getNumWeaponSlots());
					if (inflicted instanceof PlayerBodyData && s.equals(this)) {
						PlayerBodyData player = (PlayerBodyData)inflicted;
						
						player.emptySlot(player.getNumWeaponSlots());
						player.emptySlot(player.getNumWeaponSlots() + 1);
					}
				}
				
				@Override
				public void statChanges(){
					super.statChanges();
					inflicted.setStat(Stats.AMMO_CAPACITY, inflicted.getStat(Stats.AMMO_CAPACITY) + bonusAmmo);
					inflicted.setStat(Stats.RANGED_ATK_SPD, inflicted.getStat(Stats.RANGED_ATK_SPD) + bonusAtkSpd);
					inflicted.setStat(Stats.RANGED_RELOAD, inflicted.getStat(Stats.RANGED_RELOAD) + bonusReloadSpd);
				}
			};
		return enchantment;
	}
}
