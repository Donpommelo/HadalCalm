package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.UnlocktoItem;

public class SkippersBoxofFun extends Artifact {

	private final static String name = "Skipper's Box of Fun";
	private final static String descr = "Weapon Rerolling";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public SkippersBoxofFun() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new Status(state, name, descr, b) {
			
			@Override
			public void onShoot(Equipable tool) {
				
				if (tool instanceof RangedWeapon && inflicted.getSchmuck() instanceof Player) {
					
					if (((RangedWeapon)tool).getClipLeft() == 1) {
						Equipable equip = UnlocktoItem.getUnlock(UnlockEquip.valueOf(PickupEquip.getRandWeapFromPool("")), null);
						((Player)inflicted.getSchmuck()).getPlayerData().pickup(equip);
					}
				}
			}
		});
		return enchantment;
	}
}
