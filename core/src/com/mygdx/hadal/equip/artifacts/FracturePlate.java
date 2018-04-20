package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.FracturePlateStatus;

public class FracturePlate extends Artifact {

	private final static String name = "Fracture Plate";
	private final static String descr = "Blocks damge every 8 seconds";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public FracturePlate() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new FracturePlateStatus(state, b, b, 50);
		return enchantment;
	}
}
