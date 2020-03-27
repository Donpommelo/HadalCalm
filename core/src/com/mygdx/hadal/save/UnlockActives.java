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
	DEPTH_CHARGE(DepthCharge.class),
	DODGE_ROLL(DodgeRoll.class),
	HEALING_FIELD(HealingField.class),
	HYDRAULIC_UPPERCUT(HydraulicUppercut.class),
	MELON(Melon.class),
	MERIDIAN_MAKER(MeridianMaker.class),
	METEOR_STRIKE(MeteorStrike.class),
	MISSILE_POD(MissilePod.class),
	NAUTICAL_MINE(NauticalMine.class),
	ORBITAL_SHIELD(OrbitalShield.class),
	RADIAL_BARRAGE(RadialBarrage.class),
	RING_OF_GYGES(RingofGyges.class),
	SPIRIT_RELEASE(SpiritRelease.class),
	STORM_CLOUD(StormCloud.class),
	TAINTED_WATER(TaintedWater.class),
	TRACTOR_BEAM(TractorBeam.class),
	
	SPRING_LOADER(SpringLoader.class),
	
	HONEYCOMB(Honeycomb.class),
	FISH_GANG(FishGang.class),
	PORTABLE_TURRET(PortableTurret.class),
	RELOADER(Reloader.class),
	RESERVED_FUEL(ReservedFuel.class),
	
	NOTHING(NothingActive.class),
	;
	
	private Class<? extends ActiveItem> active;
	private InfoItem info;
	
	UnlockActives(Class<? extends ActiveItem> active) {
		this.active = active;
	}
	
	public static Array<UnlockActives> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockActives> items = new Array<UnlockActives>();
		
		for (UnlockActives u : UnlockActives.values()) {
			
			boolean get = UnlockManager.checkTags(u.getInfo(), tags);
			
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
		
		ArrayList<UnlockTag> defaultTags = new ArrayList<UnlockTag>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		
		if (pool.equals("")) {
			Array<UnlockActives> unlocks = UnlockActives.getUnlocks(state, false, defaultTags);
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