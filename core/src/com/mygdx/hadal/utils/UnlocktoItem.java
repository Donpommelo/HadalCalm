package com.mygdx.hadal.utils;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

import java.lang.reflect.InvocationTargetException;

/**
 * This util is used to get an unlock enum and return an instance of that weapon or active item
 * @author Cribediah Crickett
 */
public class UnlocktoItem {

	public static Equippable getUnlock(UnlockEquip unlock, Schmuck schmuck) {
		try {
			Equippable newWeapon = unlock.getWeapon().getConstructor(Schmuck.class).newInstance(schmuck);
			newWeapon.setName(unlock.getInfo().getName());
			return newWeapon;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ActiveItem getUnlock(UnlockActives unlock, Schmuck schmuck) {
		try {
			ActiveItem newActive = unlock.getActive().getConstructor(Schmuck.class).newInstance(schmuck);
			newActive.setName(unlock.getInfo().getName());
			return newActive;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
