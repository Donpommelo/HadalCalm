package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

public enum UnlockEquip {
	
	AMITA_CANNON(AmitaCannon.class),
	ASSAULT_BITS(AssaultBits.class),
	BANANA(Banana.class),
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
	LASER_GUIDED_ROCKET(LaserGuidedRocket.class),
	LASER_RIFLE(LaserRifle.class),
	MACHINEGUN(Machinegun.class),
	MAELSTROM(Maelstrom.class),
	MINIGUN(Minigun.class),
	MORAYGUN(Moraygun.class),
	MORNING_STAR(MorningStar.class),
	NEMATOCYDEARM(Nematocydearm.class),
	PEARL_REVOLVER(PearlRevolver.class),
	POPPER(Popper.class),
	RIFTSPLITTER(Riftsplitter.class),
	SCRAPRIPPER(Scrapripper.class),
	SCREECHER(Screecher.class),
	SLODGEGUN(SlodgeGun.class),
	SNIPER_RIFLE(SniperRifle.class),
	SPEARGUN(Speargun.class),
	STICKY_BOMB_LAUNCHER(StickyBombLauncher.class),
	STUTTERGUN(StutterGun.class),
	TESLA_COIL(TeslaCoil.class),
	TORPEDO_LAUNCHER(TorpedoLauncher.class),
	TRICK_GUN(TrickGun.class),
	UNDERMINER(Underminer.class),
	VAJRA(Vajra.class),
	WAVE_BEAM(WaveBeam.class),
	XBOMBER(XBomber.class),
	
	NOTHING(NothingWeapon.class),
	
	;
	
	private Class<? extends Equipable> weapon;	
	private InfoItem info;
	
	UnlockEquip(Class<? extends Equipable> weapon) {
		this.weapon = weapon;
	}
	
	public static Array<UnlockEquip> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockEquip> items = new Array<UnlockEquip>();
		
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

	public static UnlockEquip getUnlockFromEquip(Class<? extends Equipable> weapon) {
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
	 * @return
	 */
	public static String getRandWeapFromPool(PlayState state, String pool) {
		
		ArrayList<UnlockTag> defaultTags = new ArrayList<UnlockTag>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		
		if (pool.equals("")) {
			Array<UnlockEquip> unlocks = UnlockEquip.getUnlocks(state, false, defaultTags);
			return unlocks.get(GameStateManager.generator.nextInt(unlocks.size)).toString();
		}
		
		ArrayList<String> weapons = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			weapons.add(id);
		}
		return weapons.get(GameStateManager.generator.nextInt(weapons.size()));
	}
	
	public Class<? extends Equipable> getWeapon() {	return weapon; }
		
	public InfoItem getInfo() {	return info; }

	public void setInfo(InfoItem info) { this.info = info; }
}
