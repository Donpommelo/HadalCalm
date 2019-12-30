package com.mygdx.hadal.utils;

import java.lang.reflect.InvocationTargetException;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

/**
 * This util is used to get an unlock enum and return an instance of that weapon, artifact, or active item
 * @author Zachary Tu
 *
 */
public class UnlocktoItem {

	public static Equipable getUnlock(UnlockEquip unlock, Schmuck schmuck) {
		try {
			Equipable newWeapon = unlock.getWeapon().getConstructor(Schmuck.class).newInstance(schmuck);
			newWeapon.setName(unlock.getName());
			return newWeapon;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ActiveItem getUnlock(UnlockActives unlock, Schmuck schmuck) {
		try {
			ActiveItem newActive = unlock.getActive().getConstructor(Schmuck.class).newInstance(schmuck);
			newActive.setName(unlock.getName());
			return newActive;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
