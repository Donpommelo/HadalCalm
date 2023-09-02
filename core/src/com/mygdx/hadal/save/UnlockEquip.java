package com.mygdx.hadal.save;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;
import com.mygdx.hadal.utils.UnlocktoItem;

/**
 * An UnlockLevel represents a single weapon in the game
 * @author Sweshini Sleldous
 */
public enum UnlockEquip {

	AMITA_CANNON(AmitaCannon.class, GameText.AMITA_CANNON, GameText.AMITA_CANNON_DESC, GameText.AMITA_CANNON_DESC_LONG),
	ASSAULT_BITS(AssaultBits.class, GameText.ASSAULT_BITS, GameText.ASSAULT_BITS_DESC, GameText.ASSAULT_BITS_DESC_LONG),
	BANANA(Banana.class, GameText.WEAPON_BANANA, GameText.WEAPON_BANANA_DESC, GameText.WEAPON_BANANA_DESC_LONG),
	BATTERING_RAM(BatteringRam.class, GameText.BATTERING_RAM, GameText.BATTERING_RAM_DESC, GameText.BATTERING_RAM_DESC_LONG,
			UnlockTag.MEDIEVAL),
	BEEGUN(BeeGun.class, GameText.BEE_GUN, GameText.BEE_GUN_DESC, GameText.BEE_GUN_DESC_LONG),
	BOILER(Boiler.class, GameText.BOILER, GameText.BOILER_DESC, GameText.BOILER_DESC_LONG),
	BOOMERANG(Boomerang.class, GameText.BOOMERANG, GameText.BOOMERANG_DESC, GameText.BOOMERANG_DESC_LONG),
	BOUNCING_BLADE(BouncingBlade.class, GameText.BOUNCING_BLADES, GameText.BOUNCING_BLADES_DESC, GameText.BOUNCING_BLADES_DESC_LONG),
	CHARGE_BEAM(ChargeBeam.class, GameText.CHARGE_BEAM, GameText.CHARGE_BEAM_DESC, GameText.CHARGE_BEAM_DESC_LONG),
	COLACANNON(ColaCannon.class, GameText.COLA_CANNON, GameText.COLA_CANNON_DESC, GameText.COLA_CANNON_DESC_LONG),
	CR4PCANNON(CR4PCannon.class, GameText.CR4P_CANNON, GameText.CR4P_CANNON_DESC, GameText.CR4P_CANNON_DESC_LONG),
	DEEP_SEA_SMELTER(DeepSeaSmelter.class, GameText.DEEP_SEA_SMELTER, GameText.DEEP_SEA_SMELTER_DESC, GameText.DEEP_SEA_SMELTER_DESC_LONG),
	DIAMOND_CUTTER(DiamondCutter.class, GameText.DIAMOND_CUTTER, GameText.DIAMOND_CUTTER_DESC, GameText.DIAMOND_CUTTER_DESC_LONG,
			UnlockTag.MEDIEVAL),
	DIATOM_BURST(DiatomBurst.class, GameText.DIATOM_BURST, GameText.DIATOM_BURST_DESC, GameText.DIATOM_BURST_DESC_LONG),
	DUELING_CORKGUN(DuelingCorkgun.class, GameText.DUELING_CORKGUN, GameText.DUELING_CORKGUN_DESC, GameText.DUELING_CORKGUN_DESC_LONG),
	FISTICUFFS(Fisticuffs.class, GameText.FISTICUFFS, GameText.FISTICUFFS_DESC, GameText.FISTICUFFS_DESC_LONG,
			false, true, UnlockTag.MEDIEVAL),
	FLOUNDERBUSS(Flounderbuss.class, GameText.FLOUNDERBUSS, GameText.FLOUNDERBUSS_DESC, GameText.FLOUNDERBUSS_DESC_LONG),
	FUGUN(Fugun.class, GameText.FUGUN, GameText.FUGUN_DESC, GameText.FUGUN_DESC_LONG),
	GRENADE_LAUNCHER(GrenadeLauncher.class, GameText.GRENADE_LAUNCHER, GameText.GRENADE_LAUNCHER_DESC, GameText.GRENADE_LAUNCHER_DESC_LONG),
	HEXENHOWITZER(Hexenhowitzer.class, GameText.HEXENHOWITZER, GameText.HEXENHOWITZER_DESC, GameText.HEXENHOWITZER_DESC_LONG),
	ICEBERG(Iceberg.class, GameText.ICEBERG, GameText.ICEBERG_DESC, GameText.ICEBERG_DESC_LONG),
	IRON_BALL_LAUNCHER(IronBallLauncher.class, GameText.IRON_BALL_LAUNCHER, GameText.IRON_BALL_LAUNCHER_DESC, GameText.IRON_BALL_LAUNCHER_DESC_LONG),
	KAMABOKANNON(Kamabokannon.class, GameText.KAMABOKANNON, GameText.KAMABOKANNON_DESC, GameText.KAMABOKANNON_DESC_LONG),
	KILLER_BEAT(KillerBeat.class, GameText.KILLER_BEAT, GameText.KILLER_BEAT_DESC, GameText.KILLER_BEAT_DESC_LONG),
	LASER_GUIDED_ROCKET(LaserGuidedRocket.class, GameText.LASER_GUIDED_ROCKET, GameText.LASER_GUIDED_ROCKET_DESC, GameText.LASER_GUIDED_ROCKET_DESC_LONG),
	LASER_RIFLE(LaserRifle.class, GameText.LASER_RIFLE, GameText.LASER_RIFLE_DESC, GameText.LASER_RIFLE_DESC_LONG),
	LOVE_BOW(LoveBow.class, GameText.LOVE_BOW, GameText.LOVE_BOW_DESC, GameText.LOVE_BOW_DESC_LONG,
			UnlockTag.MEDIEVAL),
	MACHINEGUN(Machinegun.class, GameText.MACHINE_GUN, GameText.MACHINE_GUN_DESC, GameText.MACHINE_GUN_DESC_LONG),
	MAGIC_BEANSTALKER(MagicBeanstalker.class, GameText.MAGIC_BEANSTALKER, GameText.MAGIC_BEANSTALKER_DESC, GameText.MAGIC_BEANSTALKER_DESC_LONG),
	MAELSTROM(Maelstrom.class, GameText.MAELSTROM, GameText.MAELSTROM_DESC, GameText.MAELSTROM_DESC_LONG),
	MINIGUN(Minigun.class, GameText.MINIGUN, GameText.MINIGUN_DESC, GameText.MINIGUN_DESC_LONG),
	MORAYGUN(Moraygun.class, GameText.MORAYGUN, GameText.MORAYGUN_DESC, GameText.MORAYGUN_DESC_LONG),
	MORNING_STAR(MorningStar.class, GameText.MORNING_STAR, GameText.MORNING_STAR_DESC, GameText.MORNING_STAR_DESC_LONG,
			UnlockTag.MEDIEVAL),
	NEBULIZER(Nebulizer.class, GameText.NEBULIZER, GameText.NEBULIZER_DESC, GameText.NEBULIZER_DESC_LONG),
	NEMATOCYDEARM(Nematocydearm.class, GameText.NEMATOCYDEARM, GameText.NEMATOCYDEARM_DESC, GameText.NEMATOCYDEARM_DESC_LONG),
	PARTY_POPPER(PartyPopper.class, GameText.PARTY_POPPER, GameText.PARTY_POPPER_DESC, GameText.PARTY_POPPER_DESC_LONG),
	PEARL_REVOLVER(PearlRevolver.class, GameText.PEARL_REVOLVER, GameText.PEARL_REVOLVER_DESC, GameText.PEARL_REVOLVER_DESC_LONG),
	PEPPERGRINDER(Peppergrinder.class, GameText.PEPPERGRINDER, GameText.PEPPERGRINDER_DESC, GameText.PEPPERGRINDER_DESC_LONG),
	PUFFBALLER(Puffballer.class, GameText.PUFFBALLER, GameText.PUFFBALLER_DESC, GameText.PUFFBALLER_DESC_LONG),
	RECOMBINANT_SHOTRIFLE(RecombinantShotrifle.class, GameText.RECOMBINANT_SHOTRIFLE, GameText.RECOMBINANT_SHOTRIFLE_DESC, GameText.RECOMBINANT_SHOTRIFLE_DESC_LONG),
	RETICLE_STRIKE(ReticleStrike.class, GameText.RETICLE_STRIKE, GameText.RETICLE_STRIKE_DESC, GameText.RETICLE_STRIKE_DESC_LONG),
	RIFTSPLITTER(Riftsplitter.class, GameText.RIFTSPLITTER, GameText.RIFTSPLITTER_DESC, GameText.RIFTSPLITTER_DESC_LONG),
	SCRAPRIPPER(Scrapripper.class, GameText.SCRAPRIPPER, GameText.SCRAPRIPPER_DESC, GameText.SCRAPRIPPER_DESC_LONG,
			UnlockTag.MEDIEVAL),
	SCREECHER(Screecher.class, GameText.SCREECHER, GameText.SCREECHER_DESC, GameText.SCREECHER_DESC_LONG),
	SLODGE_NOZZLE(SlodgeNozzle.class, GameText.SLODGE_NOZZLE, GameText.SLODGE_NOZZLE_DESC, GameText.SLODGE_NOZZLE_DESC_LONG),
	SNIPER_RIFLE(SniperRifle.class, GameText.SNIPER_RIFLE, GameText.SNIPER_RIFLE_DESC, GameText.SNIPER_RIFLE_DESC_LONG),
	SPEARGUN(Speargun.class, GameText.SPEARGUN, GameText.SPEARGUN_DESC, GameText.SPEARGUN_DESC_LONG,
			false, true),
	STICKY_BOMB_LAUNCHER(StickyBombLauncher.class, GameText.STICKYBOMB_LAUNCHER, GameText.STICKYBOMB_LAUNCHER_DESC, GameText.STICKYBOMB_LAUNCHER_DESC_LONG),
	STUTTERGUN(StutterGun.class, GameText.STUTTERGUN, GameText.STUTTERGUN_DESC, GameText.STUTTERGUN_DESC_LONG),
	TESLA_COIL(TeslaCoil.class, GameText.TESLA_COIL, GameText.TESLA_COIL_DESC, GameText.TESLA_COIL_DESC_LONG),
	TORPEDO_LAUNCHER(TorpedoLauncher.class, GameText.TORPEDO_LAUNCHER, GameText.TORPEDO_LAUNCHER_DESC, GameText.TORPEDO_LAUNCHER_DESC_LONG),
	TRICK_GUN(TrickGun.class, GameText.TRICK_GUN, GameText.TRICK_GUN_DESC, GameText.TRICK_GUN_DESC_LONG),
	TRIDENT(Trident.class, GameText.TRIDENT, GameText.TRIDENT_DESC, GameText.TRIDENT_DESC_LONG),
	UNDERMINER(Underminer.class, GameText.UNDERMINER, GameText.UNDERMINER_DESC, GameText.UNDERMINER_DESC_LONG),
	URCHIN_NAILGUN(UrchinNailgun.class, GameText.URCHIN_NAILGUN, GameText.URCHIN_NAILGUN_DESC, GameText.URCHIN_NAILGUN_DESC_LONG),
	VAJRA(Vajra.class, GameText.VAJRA, GameText.VAJRA_DESC, GameText.VAJRA_DESC_LONG),
	WAVE_BEAM(WaveBeam.class, GameText.WAVE_BEAM, GameText.WAVE_BEAM_DESC, GameText.WAVE_BEAM_DESC_LONG),
	XBOMBER(XBomber.class, GameText.X_BOMBER, GameText.X_BOMBER_DESC, GameText.X_BOMBER_DESC_LONG),
	
	NOTHING(NothingWeapon.class, GameText.NOTHING, GameText.NOTHING, GameText.NOTHING,
			false, true),
	SPEARGUN_NERFED(SpeargunNerfed.class, GameText.SPEARGUN, GameText.SPEARGUN_DESC, GameText.SPEARGUN_DESC_LONG,
			true, true),

	;
	
	//the weapon that this unlock represents
	private final Class<? extends Equippable> weapon;
	private final Equippable equipSingleton;
	
	//the weapon's information
	private final GameText name, desc, descLong;
	private final Array<UnlockTag> tags = new Array<>();

	UnlockEquip(Class<? extends Equippable> weapon, GameText name, GameText desc, GameText descLong,
				boolean omitHub, boolean omitRandom, UnlockTag... tags) {
		this.weapon = weapon;
		this.name = name;
		this.desc = desc;
		this.descLong = descLong;
		if (!omitHub) {
			this.tags.add(UnlockTag.ARMORY);
		}
		if (!omitRandom) {
			this.tags.add(UnlockTag.RANDOM_POOL);
		}
		this.tags.addAll(tags);
		this.equipSingleton = UnlocktoItem.getUnlock(this, null);
	}

	UnlockEquip(Class<? extends Equippable> weapon, GameText name, GameText desc, GameText descLong, UnlockTag... tags) {
		this(weapon, name, desc, descLong, false, false, tags);
	}

	/**
	 * This acquires a list of all unlocked weapons (if unlock is true. otherwise just return all weapons that satisfy the tags)
	 */
	public static Array<UnlockEquip> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
		Array<UnlockEquip> items = new Array<>();
		
		for (UnlockEquip u : UnlockEquip.values()) {
			
			boolean get = UnlockManager.checkTags(u.tags, tags);
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.EQUIP, u.toString())) {
				get = false;
			}
			if (get) {
				items.add(u);
			}
		}
		return items;
	}

	/**
	 * This method returns the unlock corresponding to a specific weapon
	 */
	public static UnlockEquip getUnlockFromEquip(Class<? extends Equippable> weapon) {
		for (UnlockEquip unlock : UnlockEquip.values()) {
			if (unlock.weapon.equals(weapon)) {
				return unlock;
			}
		}
		return null;
	}
	
	/**
	 * This method returns the name of a weapon randomly selected from the pool.
	 * @param pool: comma separated list of names of weapons to choose from. if set to "", return any weapon in the random pool.
	 */
	public static UnlockEquip getRandWeapFromPool(PlayState state, String pool) {

		Array<UnlockTag> defaultTags = new Array<>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		defaultTags.addAll(state.getMapEquipTag());

		if ("".equals(pool)) {
			Array<UnlockEquip> unlocks = UnlockEquip.getUnlocks(state, false, defaultTags);
			return unlocks.get(MathUtils.random(unlocks.size - 1));
		}

		Array<String> weapons = new Array<>();
		weapons.addAll(pool.split(","));
		return UnlockEquip.getByName(weapons.get(MathUtils.random(weapons.size - 1)));
	}
	
	public Class<? extends Equippable> getWeapon() { return weapon; }
		
	public String getName() { return name.text(); }

	public String getDesc() { return desc.text(); }

	/**
	 * Get description and fill wildcards with item information
	 */
	public String getDescLong() { return descLong.text(equipSingleton.getDescFields()); }

	private static final ObjectMap<String, UnlockEquip> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockEquip u : UnlockEquip.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockEquip getByName(String s) {
		return UnlocksByName.get(s, NOTHING);
	}
}
