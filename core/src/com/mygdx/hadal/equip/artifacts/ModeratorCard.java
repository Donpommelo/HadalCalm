package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.PickupWeaponMod;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class ModeratorCard extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static int numUpgrades = 3;
	
	public ModeratorCard() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public void levelStart() {
				
				if (inflicted.getSchmuck() instanceof Player) {
					
					for (Equipable e : ((Player)inflicted.getSchmuck()).getPlayerData().getMultitools()) {
						if (!(e instanceof NothingWeapon)) {
							for (int i = 0; i < numUpgrades; i++) {
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
