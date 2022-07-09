package com.mygdx.hadal.save;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.*;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;
import com.mygdx.hadal.utils.UnlocktoItem;

/**
 * An UnlockArtifact represents a single artifact in the game
 * @author Squirnelius Svarnaise
 */
public enum UnlockActives {
	
	ANCHOR_SMASH(AnchorSmash.class, GameText.ANCHOR_SMASH, GameText.ANCHOR_SMASH_DESC, GameText.ANCHOR_SMASH_DESC_LONG),
	AUTORELOADER(Reloader.class, GameText.RELOADER, GameText.RELOADER_DESC, GameText.RELOADER_DESC_LONG),
	BENDY_BEAMS(BendyBeams.class, GameText.BENDY_BEAMS, GameText.BENDY_BEAMS_DESC, GameText.BENDY_BEAMS_DESC_LONG),
	CALL_OF_WALRUS(CallofWalrus.class, GameText.CALL_OF_WALRUS, GameText.CALL_OF_WALRUS_DESC, GameText.CALL_OF_WALRUS_DESC_LONG),
	DEPTH_CHARGE(DepthCharge.class, GameText.DEPTH_CHARGE, GameText.DEPTH_CHARGE_DESC, GameText.DEPTH_CHARGE_DESC_LONG),
	FAFROTSKIES(Fafrotskies.class, GameText.FAFROTSKIES, GameText.FAFROTSKIES_DESC, GameText.FAFROTSKIES_DESC_LONG),
	FISH_GANG(FishGang.class, GameText.FISH_GANG, GameText.FISH_GANG_DESC, GameText.FISH_GANG_DESC_LONG),
	FLASH_BANG(Flashbang.class, GameText.FLASH_BANG, GameText.FLASH_BANG_DESC, GameText.FLASH_BANG_DESC_LONG),
	FORCE_OF_WILL(ForceofWill.class, GameText.FORCE_OF_WILL, GameText.FORCE_OF_WILL_DESC, GameText.FORCE_OF_WILL_DESC_LONG),
	GHOST_STEP(GhostStep.class, GameText.GHOST_STEP, GameText.GHOST_STEP_DESC, GameText.GHOST_STEP_DESC_LONG),
	HEALING_FIELD(HealingField.class, GameText.HEALING_FIELD, GameText.HEALING_FIELD_DESC, GameText.HEALING_FIELD_DESC_LONG),
	HONEYCOMB(Honeycomb.class, GameText.HONEYCOMB, GameText.HONEYCOMB_DESC, GameText.HONEYCOMB_DESC_LONG),
	HYDRAULIC_UPPERCUT(HydraulicUppercut.class, GameText.HYDRAULIC_UPPERCUT, GameText.HYDRAULIC_UPPERCUT_DESC, GameText.HYDRAULIC_UPPERCUT_DESC_LONG),
	IMMOLATION_AURA(ImmolationAura.class, GameText.IMMOLATION_AURA, GameText.IMMOLATION_AURA_DESC, GameText.IMMOLATION_AURA_DESC_LONG),
	JUMP_KICK(JumpKick.class, GameText.JUMP_KICK, GameText.JUMP_KICK_DESC, GameText.JUMP_KICK_DESC_LONG),
	MARINE_SNOWGLOBE(MarineSnowglobe.class, GameText.MARINE_SNOWGLOBE, GameText.MARINE_SNOWGLOBE_DESC, GameText.MARINE_SNOWGLOBE_DESC_LONG),
	MELON(Melon.class, GameText.MELON, GameText.MELON_DESC, GameText.MELON_DESC_LONG),
	MERIDIAN_MAKER(MeridianMaker.class, GameText.MERIDIAN_MAKER, GameText.MERIDIAN_MAKER_DESC, GameText.MERIDIAN_MAKER_DESC_LONG),
	METEOR_STRIKE(MeteorStrike.class, GameText.METEOR_STRIKE, GameText.METEOR_STRIKE_DESC, GameText.METEOR_STRIKE_DESC_LONG),
	MISSILE_POD(MissilePod.class, GameText.MISSILE_POD, GameText.MISSILE_POD_DESC, GameText.MISSILE_POD_DESC_LONG),
	NAUTICAL_MINE(NauticalMine.class, GameText.NAUTICAL_MINE, GameText.NAUTICAL_MINE_DESC, GameText.NAUTICAL_MINE_DESC_LONG),
	ORBITAL_SHIELD(OrbitalShield.class, GameText.ORBITAL_SHIELD, GameText.ORBITAL_SHIELD_DESC, GameText.ORBITAL_SHIELD_DESC_LONG),
	PLUS_MINUS(PlusMinus.class, GameText.PLUS_MINUS, GameText.PLUS_MINUS_DESC, GameText.PLUS_MINUS_DESC_LONG),
	PORTABLE_SENTRY(PortableSentry.class, GameText.PORTABLE_SENTRY, GameText.PORTABLE_SENTRY_DESC, GameText.PORTABLE_SENTRY_DESC_LONG),
	PROXIMITY_MINE(ProximityMine.class, GameText.PROXIMITY_MINE, GameText.PROXIMITY_MINE_DESC, GameText.PROXIMITY_MINE_DESC_LONG),
	RADIAL_BARRAGE(RadialBarrage.class, GameText.RADIAL_BARRAGE, GameText.RADIAL_BARRAGE_DESC, GameText.RADIAL_BARRAGE_DESC_LONG),
	RESERVED_FUEL(ReservedFuel.class, GameText.RESERVED_FUEL, GameText.RESERVED_FUEL_DESC, GameText.RESERVED_FUEL_DESC_LONG),
	RING_OF_GYGES(RingofGyges.class, GameText.RING_OF_GYGES, GameText.RING_OF_GYGES_DESC, GameText.RING_OF_GYGES_DESC_LONG),
	SAMSON_OPTION(SamsonOption.class, GameText.SAMSON_OPTION, GameText.SAMSON_OPTION_DESC, GameText.SAMSON_OPTION_DESC_LONG,
			false, true),
	SPIRIT_RELEASE(SpiritRelease.class, GameText.SPIRIT_RELEASE, GameText.SPIRIT_RELEASE_DESC, GameText.SPIRIT_RELEASE_DESC_LONG),
	SPRING_LOADER(SpringLoader.class, GameText.SPRING_LOADER, GameText.SPRING_LOADER_DESC, GameText.SPRING_LOADER_DESC_LONG),
	SUPPLY_DROP(SupplyDrop.class, GameText.SUPPLY_DROP, GameText.SUPPLY_DROP_DESC, GameText.SUPPLY_DROP_DESC_LONG),
	TAINTED_WATER(TaintedWater.class, GameText.TAINTED_WATER, GameText.TAINTED_WATER_DESC, GameText.TAINTED_WATER_DESC_LONG),
	TERRAFORMER(Terraformer.class, GameText.TERRAFORMER, GameText.TERRAFORMER_DESC, GameText.TERRAFORMER_DESC_LONG),
	TRACTOR_BEAM(TractorBeam.class, GameText.TRACTOR_BEAM, GameText.TRACTOR_BEAM_DESC, GameText.TRACTOR_BEAM_DESC_LONG),
	
	NOTHING(NothingActive.class, GameText.NOTHING, GameText.NOTHING, GameText.NOTHING, false, true),
	;
	
	//the active item represented by this unlock
	private final Class<? extends ActiveItem> active;
	private final ActiveItem magicSingleton;
	
	//the magic's information
	private final GameText name, desc, descLong;
	private final Array<UnlockTag> tags = new Array<>();

	UnlockActives(Class<? extends ActiveItem> active, GameText name, GameText desc, GameText descLong,
				  boolean omitHub, boolean omitRandom, UnlockTag... tags) {
		this.active = active;
		this.name = name;
		this.desc = desc;
		this.descLong = descLong;
		if (!omitHub) {
			this.tags.add(UnlockTag.ARCANERY);
		}
		if (!omitRandom) {
			this.tags.add(UnlockTag.RANDOM_POOL);
		}
		this.tags.addAll(tags);
		this.magicSingleton = UnlocktoItem.getUnlock(this, null);
	}

	UnlockActives(Class<? extends ActiveItem> active, GameText name, GameText desc, GameText descLong, UnlockTag... tags) {
		this(active, name, desc, descLong, false, false, tags);
	}
	
	/**
	 * This acquires a list of all unlocked actives (if unlock is true. otherwise just return all actives that satisfy the tags)
	 */
	public static Array<UnlockActives> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
		Array<UnlockActives> items = new Array<>();
		
		for (UnlockActives u : UnlockActives.values()) {
			
			boolean get = UnlockManager.checkTags(u.tags, tags);
			
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
		for (UnlockActives unlock : UnlockActives.values()) {
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
		
		if ("".equals(pool)) {
			Array<UnlockActives> unlocks = UnlockActives.getUnlocks(state, false, defaultTags);
			return unlocks.get(MathUtils.random(unlocks.size - 1));
		}

		Array<String> weapons = new Array<>();
		weapons.addAll(pool.split(","));
		return UnlockActives.getByName(weapons.get(MathUtils.random(weapons.size - 1)));
	}
	
	public Class<? extends ActiveItem> getActive() { return active; }

	public String getName() { return name.text(); }

	public String getDesc() { return desc.text(); }

	/**
	 * Get description and fill wildcards with item information
	 */
	public String getDescLong() { return descLong.text(magicSingleton.getDescFields()); }

	private static final ObjectMap<String, UnlockActives> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockActives u : UnlockActives.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockActives getByName(String s) { return UnlocksByName.get(s, NOTHING); }
}