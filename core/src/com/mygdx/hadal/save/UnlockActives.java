package com.mygdx.hadal.save;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.*;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

/**
 * An UnlockArtifact represents a single artifact in the game
 * @author Squirnelius Svarnaise
 */
public enum UnlockActives {
	
	ANCHOR_SMASH(AnchorSmash.class),
	RELOADER(Reloader.class),
	BENDY_BEAMS(BendyBeams.class),
	CALL_OF_WALRUS(CallofWalrus.class),
	DEPTH_CHARGE(DepthCharge.class),
	DODGE_ROLL(DodgeRoll.class),
	FAFROTSKIES(Fafrotskies.class),
	FISH_GANG(FishGang.class),
	FLASH_BANG(Flashbang.class),
	FORCE_OF_WILL(ForceofWill.class),
	HEALING_FIELD(HealingField.class),
	HONEYCOMB(Honeycomb.class),
	HYDRAULIC_UPPERCUT(HydraulicUppercut.class),
	IMMOLATION_AURA(ImmolationAura.class),
	JUMP_KICK(JumpKick.class),
	MARINE_SNOWGLOBE(MarineSnowglobe.class),
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
	RESERVED_FUEL(ReservedFuel.class),
	RING_OF_GYGES(RingofGyges.class),
	SAMSON_OPTION(SamsonOption.class),
	SPIRIT_RELEASE(SpiritRelease.class),
	SPRING_LOADER(SpringLoader.class),
	SUPPLY_DROP(SupplyDrop.class),
	TAINTED_WATER(TaintedWater.class),
	TERRAFORMER(Terraformer.class),
	TRACTOR_BEAM(TractorBeam.class),
	
	NOTHING(NothingActive.class),
	;
	
	//the active item represented by this unlock
	private final Class<? extends ActiveItem> active;
	
	//the active item's information
	private InfoItem info;
	
	UnlockActives(Class<? extends ActiveItem> active) {
		this.active = active;
	}
	
	/**
	 * This acquires a list of all unlocked actives (if unlock is true. otherwise just return all actives that satisfy the tags)
	 */
	public static Array<UnlockActives> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
		Array<UnlockActives> items = new Array<>();
		
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
	 * This method returns the unlock corresponding to a specific active item
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
	 * @return the string name of the randomly selected item
	 */
	public static UnlockActives getRandItemFromPool(PlayState state, String pool) {

		Array<UnlockTag> defaultTags = new Array<>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		
		if (pool.equals("")) {
			Array<UnlockActives> unlocks = UnlockActives.getUnlocks(state, false, defaultTags);
			return unlocks.get(MathUtils.random(unlocks.size - 1));
		}

		Array<String> weapons = new Array<>();
		weapons.addAll(pool.split(","));
		return UnlockActives.getByName(weapons.get(MathUtils.random(weapons.size - 1)));
	}
	
	public Class<? extends ActiveItem> getActive() { return active; }
	
	public InfoItem getInfo() {	return info; }
	
	public void setInfo(InfoItem info) { this.info = info; }

	private static final ObjectMap<String, UnlockActives> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockActives u: UnlockActives.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockActives getByName(String s) { return UnlocksByName.get(s, NOTHING); }
}