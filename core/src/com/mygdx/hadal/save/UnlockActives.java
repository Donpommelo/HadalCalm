package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.*;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * An UnlockArtifact represents a single artifact in the game
 * @author Zachary Tu
 */
public enum UnlockActives {
	
	ANCHOR_SMASH(AnchorSmash.class),
	BENDY_BEAMS(BendyBeams.class),
	CALL_OF_WALRUS(CallofWalrus.class),
	DEPTH_CHARGE(DepthCharge.class),
	DODGE_ROLL(DodgeRoll.class),
	FAFROTSKIES(Fafrotskies.class),
	FISH_GANG(FishGang.class),
	FORCE_OF_WILL(ForceofWill.class),
	HEALING_FIELD(HealingField.class),
	HONEYCOMB(Honeycomb.class),
	HYDRAULIC_UPPERCUT(HydraulicUppercut.class),
	JUMP_KICK(JumpKick.class),
	MELON(Melon.class),
	MERIDIAN_MAKER(MeridianMaker.class),
	METEOR_STRIKE(MeteorStrike.class),
	MISSILE_POD(MissilePod.class),
	NAUTICAL_MINE(NauticalMine.class),
	ORBITAL_SHIELD(OrbitalShield.class),
	PLUS_MINUS(PlusMinus.class),
	PORTABLE_SENTRY_FLAK(PortableSentryFlak.class),
	PORTABLE_SENTRY_VOLLEY(PortableSentryVolley.class),
	PROXIMITY_MINE(ProximityMine.class),
	RADIAL_BARRAGE(RadialBarrage.class),
	RELOADER(Reloader.class),
	RESERVED_FUEL(ReservedFuel.class),
	RING_OF_GYGES(RingofGyges.class),
	SPIRIT_RELEASE(SpiritRelease.class),
	SPRING_LOADER(SpringLoader.class),
	TAINTED_WATER(TaintedWater.class),
	TERRAFORMER(Terraformer.class),
	TRACTOR_BEAM(TractorBeam.class),
	
	NOTHING(NothingActive.class),
	;
	
	//the active item represented by this unlock
	private Class<? extends ActiveItem> active;
	
	//the active item's information
	private InfoItem info;
	
	UnlockActives(Class<? extends ActiveItem> active) {
		this.active = active;
	}
	
	/**
	 * This acquires a list of all unlocked actives (if unlock is true. otherwise just return all actives that satisfy the tags)
	 */
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
	
	/**
	 * This method returns the unlock corresponding to a specfic active item
	 */
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
			return unlocks.get(GameStateManager.generator.nextInt(unlocks.size)).toString();
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