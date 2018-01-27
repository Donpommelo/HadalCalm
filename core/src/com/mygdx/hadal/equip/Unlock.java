package com.mygdx.hadal.equip;

public enum Unlock {
	
	
	;
	
	private Class<? extends Equipable> weapon;
	private boolean unlocked;
	
	Unlock(Class<? extends Equipable> weapon) {
		this.weapon = weapon;
		this.unlocked = false;
	}

	public Class<? extends Equipable> getWeapon() {
		return weapon;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
}
