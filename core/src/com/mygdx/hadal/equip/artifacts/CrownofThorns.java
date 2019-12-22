package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CrownofThorns extends Artifact {

	private final static String name = "Crown of Thorns";
	private final static String descr = "Damages nearby enemies when reloading.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	

	
	public CrownofThorns() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onReload(Equipable tool) {

			}
		};
		return enchantment;
	}
}
