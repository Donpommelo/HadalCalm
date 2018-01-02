package com.mygdx.hadal.equip;

import com.mygdx.hadal.equip.enemy.Nothing;
import com.mygdx.hadal.equip.melee.Scrapripper;
import com.mygdx.hadal.equip.misc.MomentumShooter;
import com.mygdx.hadal.equip.ranged.Speargun;

public class Loadout {

	private final static int numSlots = 4;
	
	public Class<? extends Equipable> slot1, slot2, slot3, slot4;
	
	public Loadout() {
		slot1 = Speargun.class;
		slot2 = Scrapripper.class;
		slot3 = MomentumShooter.class;
		slot4 = Nothing.class;
	}

	public Loadout(Class<? extends Equipable>... slots) {
		slot1 = slots[0];
		slot2 = slots[1];
		slot3 = slots[2];
		slot4 = slots[3];
	}
	
	public static int getNumslots() {
		return numSlots;
	}
}
