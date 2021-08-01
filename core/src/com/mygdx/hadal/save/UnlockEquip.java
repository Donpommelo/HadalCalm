package com.mygdx.hadal.save;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * An UnlockLevel represents a single weapon in the game
 * @author Sweshini Sleldous
 */
public enum UnlockEquip {

	AMITA_CANNON(AmitaCannon.class),
	ASSAULT_BITS(AssaultBits.class),
	BANANA(Banana.class),
	BATTERING_RAM(BatteringRam.class),
	BEEGUN(BeeGun.class),
	BOILER(Boiler.class),
	BOOMERANG(Boomerang.class),
	BOUNCING_BLADE(BouncingBlade.class),
	CHARGE_BEAM(ChargeBeam.class),
	COLACANNON(ColaCannon.class),
	CR4PCANNON(CR4PCannon.class),
	DEEP_SEA_SMELTER(DeepSeaSmelter.class),
	DUELING_CORKGUN(DuelingCorkgun.class),
	DIAMOND_CUTTER(DiamondCutter.class),
	FISTICUFFS(Fisticuffs.class),
	FLOUNDERBUSS(Flounderbuss.class),
	FUGUN(Fugun.class),
	GRENADE_LAUNCHER(GrenadeLauncher.class),
	HEXENHOWITZER(Hexenhowitzer.class),
	ICEBERG(Iceberg.class),
	IRON_BALL_LAUNCHER(IronBallLauncher.class),
	KAMABOKANNON(Kamabokannon.class),
	KILLER_BEAT(KillerBeat.class),
	LASER_GUIDED_ROCKET(LaserGuidedRocket.class),
	LASER_RIFLE(LaserRifle.class),
	LOVE_BOW(LoveBow.class),
	MACHINEGUN(Machinegun.class),
	MAELSTROM(Maelstrom.class),
	MINIGUN(Minigun.class),
	MORAYGUN(Moraygun.class),
	MORNING_STAR(MorningStar.class),
	NEMATOCYDEARM(Nematocydearm.class),
	PARTY_POPPER(PartyPopper.class),
	PEARL_REVOLVER(PearlRevolver.class),
	PEPPERGRINDER(Peppergrinder.class),
	PUFFBALLER(Puffballer.class),
	RETICLE_STRIKE(ReticleStrike.class),
	RIFTSPLITTER(Riftsplitter.class),
	SCRAPRIPPER(Scrapripper.class),
	SCREECHER(Screecher.class),
	SLODGE_NOZZLE(SlodgeNozzle.class),
	SNIPER_RIFLE(SniperRifle.class),
	SPEARGUN(Speargun.class),
	STICKY_BOMB_LAUNCHER(StickyBombLauncher.class),
	STUTTERGUN(StutterGun.class),
	TESLA_COIL(TeslaCoil.class),
	TORPEDO_LAUNCHER(TorpedoLauncher.class),
	TRICK_GUN(TrickGun.class),
	TYRRAZZAN_REAPER(TyrrazzanReaper.class),
	UNDERMINER(Underminer.class),
	VINE_SOWER(VineSower.class),
	VAJRA(Vajra.class),
	WAVE_BEAM(WaveBeam.class),
	XBOMBER(XBomber.class),
	
	NOTHING(NothingWeapon.class),
	SPEARGUN_NERFED(SpeargunNerfed.class),

	;
	
	//the weapon that this unlock represents
	private final Class<? extends Equippable> weapon;
	
	//the weapon's information
	private InfoItem info;
	
	UnlockEquip(Class<? extends Equippable> weapon) {
		this.weapon = weapon;
	}
	
	/**
	 * This acquires a list of all unlocked weapons (if unlock is true. otherwise just return all weapons that satisfy the tags)
	 */
	public static Array<UnlockEquip> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockEquip> items = new Array<>();
		
		for (UnlockEquip u : UnlockEquip.values()) {
			
			boolean get = UnlockManager.checkTags(u.getInfo(), tags);
			
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
		for (UnlockEquip unlock: UnlockEquip.values()) {
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
	public static String getRandWeapFromPool(PlayState state, String pool) {
		
		ArrayList<UnlockTag> defaultTags = new ArrayList<>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		
		if (pool.equals("")) {
			Array<UnlockEquip> unlocks = UnlockEquip.getUnlocks(state, false, defaultTags);
			return unlocks.get(MathUtils.random(unlocks.size - 1)).toString();
		}

		ArrayList<String> weapons = new ArrayList<>();

		Collections.addAll(weapons, pool.split(","));
		return weapons.get(MathUtils.random(weapons.size() - 1));
	}
	
	public Class<? extends Equippable> getWeapon() { return weapon; }
		
	public InfoItem getInfo() {	return info; }

	public void setInfo(InfoItem info) { this.info = info; }

	private static final HashMap<String, UnlockEquip> UnlocksByName = new HashMap<>();
	static {
		for (UnlockEquip u: UnlockEquip.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockEquip getByName(String s) {
		return UnlocksByName.getOrDefault(s, NOTHING);
	}
}
