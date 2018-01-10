package com.mygdx.hadal.equip;

import com.mygdx.hadal.equip.melee.Scrapripper;
import com.mygdx.hadal.equip.misc.MomentumShooter;
import com.mygdx.hadal.equip.ranged.Speargun;
import com.mygdx.hadal.managers.AssetList;

public class Loadout {

	private final static int numSlots = 4;
	
	public Equipable[] multitools;

	public String playerSprite;
	
	public Loadout() {
		multitools = new Equipable[numSlots];
		multitools[0] = new Speargun(null);
		multitools[1] = new Scrapripper(null);
		multitools[2] = new MomentumShooter(null);
		playerSprite = AssetList.PLAYER_MOREAU_ATL.toString();
	}
	
	public static int getNumSlots() {
		return numSlots;
	}
}
