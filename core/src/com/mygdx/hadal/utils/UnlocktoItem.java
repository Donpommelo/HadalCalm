package com.mygdx.hadal.utils;

import java.lang.reflect.InvocationTargetException;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class UnlocktoItem {

	
	public static Equipable getUnlock(UnlockEquip unlock, Schmuck schmuck) {
		try {
			return unlock.getWeapon().getConstructor(Schmuck.class).newInstance(schmuck);
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
