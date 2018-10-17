package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.misc.Nothing;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.PickupWeaponMod;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class ModeratorCard extends Artifact {

	private final static String name = "Moderator Card";
	private final static String descr = "Upgrade Your Weapons at the Start of Level.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public ModeratorCard() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new Status(state, name, descr, b) {
			
			@Override
			public void levelStart() {
				
				if (inflicted.getSchmuck() instanceof Player) {
					
					for (Equipable e : ((Player)inflicted.getSchmuck()).getPlayerData().getMultitools()) {
						if (!(e instanceof Nothing)) {
							for (int i = 0; i < 2; i++) {
								WeaponMod mod = WeaponMod.valueOf(PickupWeaponMod.getRandModFromPool("", ModTag.RANDOM_POOL));
								mod.acquireMod(((Player)inflicted.getSchmuck()).getPlayerData(), state, e);
							}
						}
					}
				}
				
			}
		});
		return enchantment;
	}
}
