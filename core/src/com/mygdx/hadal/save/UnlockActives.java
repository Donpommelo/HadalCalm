package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.*;
import com.mygdx.hadal.managers.GameStateManager;

public enum UnlockActives {
	
	ANCHOR_SMASH(AnchorSmash.class),
	METEOR_STRIKE(MeteorStrike.class),
	
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
	
	public static Array<UnlockActives> getUnlocks(PlayState state, boolean unlock, UnlockTag... tags) {
		Array<UnlockActives> items = new Array<UnlockActives>();
		
		for (UnlockActives u : UnlockActives.values()) {
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getInfo().getTags().size(); j++) {
					if (tags[i].equals(u.getInfo().getTags().get(j))) {
						get = true;
					}
				}
			}
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.ACTIVE, u.toString())) {
				get = false;
			}
			
			if (get) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public static UnlockActives getUnlockFromActive(Class<? extends ActiveItem> active) {
		for (UnlockActives unlock: UnlockActives.values()) {
			if (unlock.active.equals(active)) {
				return unlock;
			}
		}
		return null;
	}
	
	
	/**
	 * This method returns the name of a weapon randomly selected from the pool.
	 * @param pool: comma separated list of names of weapons to choose from. if set to "", return any weapon in the random pool.
	 * @return
	 */
	public static String getRandItemFromPool(PlayState state, String pool) {
		
		if (pool.equals("")) {
			Array<UnlockActives> unlocks = UnlockActives.getUnlocks(state, false, UnlockTag.RANDOM_POOL);
			return unlocks.get(GameStateManager.generator.nextInt(unlocks.size)).name();
		}
		
		ArrayList<String> weapons = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			weapons.add(id);
		}
		return weapons.get(GameStateManager.generator.nextInt(weapons.size()));
	}
	
	public Class<? extends ActiveItem> getActive() { return active; }
	
	public InfoItem getInfo() {	return info; }
	
	public void setInfo(InfoItem info) { this.info = info; }
}