package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.AnarchistCookbookStatus;

public class AnarchistsCookbook extends Artifact {

	private final static String name = "Anarchist's Cookbook";
	private final static String descr = "Explosions";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public AnarchistsCookbook() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new AnarchistCookbookStatus(state, b, b, 50);
		return enchantment;
	}
}
