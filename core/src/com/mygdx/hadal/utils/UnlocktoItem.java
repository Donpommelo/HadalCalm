package com.mygdx.hadal.utils;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.actives.NothingActive;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * This util is used to get an unlock enum and return an instance of that weapon or active item
 * @author Cribediah Crickett
 */
public class UnlocktoItem {

	public static Equippable getUnlock(UnlockEquip unlock, Player player) {
		try {
			Equippable newWeapon = unlock.getWeapon().getConstructor(Player.class).newInstance(player);
			newWeapon.setName(unlock.getName());
			return newWeapon;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return new NothingWeapon(player);
	}
	
	public static ActiveItem getUnlock(UnlockActives unlock, Player player) {
		try {
			ActiveItem newActive = unlock.getActive().getConstructor(Player.class).newInstance(player);
			newActive.setName(unlock.getName());
			return newActive;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return new NothingActive(player);
	}
}
