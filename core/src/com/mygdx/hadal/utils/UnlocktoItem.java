package com.mygdx.hadal.utils;

import java.lang.reflect.InvocationTargetException;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class UnlocktoItem {

	
	public static Equipable getUnlock(UnlockEquip unlock, Schmuck schmuck) {
		try {
			Equipable newWeapon = unlock.getWeapon().getConstructor(Schmuck.class).newInstance(schmuck);
			newWeapon.setDescr(unlock.getDescr());
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
	
	public static Artifact getUnlock(UnlockArtifact unlock) {
		try {
			return unlock.getArtifact().getConstructor().newInstance();
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
			newActive.setDescr(unlock.getDescr());
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
