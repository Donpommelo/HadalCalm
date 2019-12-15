package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.*;

public enum UnlockActives {
	
	TAINTED_WATER(TaintedWater.class),
	TRACTOR_BEAM(TractorBeam.class),
	SPRING_LOADER(SpringLoader.class),
	NAUTICAL_MINE(NauticalMine.class),
	HYDRAULIC_UPPERCUT(HydraulicUppercut.class),
	HONEYCOMB(Honeycomb.class),
	FIREBALL(Fireball.class),
	FISH_GANG(FishGang.class),
	MELON(Melon.class),
	NOTHING(NothingActive.class),
	MISSILE_POD(MissilePod.class),
	PORTABLE_TURRET(PortableTurret.class),
	RELOADER(Reloader.class),
	RESERVED_FUEL(ReservedFuel.class),

	;
	
	private Class<? extends ActiveItem> active;
	private InfoItem info;
	
	UnlockActives(Class<? extends ActiveItem> active) {
		this.active = active;
	}
	
	public static Array<UnlockActives> getUnlocks(boolean unlock, UnlockTag... tags) {
		Array<UnlockActives> items = new Array<UnlockActives>();
		
		for (UnlockActives u : UnlockActives.values()) {
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getTags().size(); j++) {
					if (tags[i].equals(u.getTags().get(j))) {
						get = true;
					}
				}
			}
			
			if (unlock && !u.isUnlocked()) {
				get = false;
			}
			
			if (get) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public Class<? extends ActiveItem> getActive() {
		return active;
	}
	
	public InfoItem getInfo() {
		return info;
	}
	
	public void setInfo(InfoItem info) {
		this.info = info;
	}
	
	public boolean isUnlocked() {
		return info.isUnlocked();
	}
	
	public ArrayList<UnlockTag> getTags() {
		return info.getTags();
	}
	
	public String getName() {
		return info.getName();
	}
	
	public String getDescr() {
		return info.getDescription();
	}
	
	public String getDescrLong() {
		return info.getDescriptionLong();
	}
	
	public int getCost() {
		return info.getCost();
	}
	
	public void setUnlocked(boolean unlock) {
		info.setUnlocked(unlock);
	}
	
	public static UnlockActives getUnlockFromActive(Class<? extends ActiveItem> active) {
		for (UnlockActives unlock: UnlockActives.values()) {
			if (unlock.active.equals(active)) {
				return unlock;
			}
		}
		return null;
	}
}