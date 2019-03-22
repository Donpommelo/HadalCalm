package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;

public enum UnlockEquip {
	
	BEEGUN(BeeGun.class),
	BOILER(Boiler.class),
	BOOMERANG(Boomerang.class),
	BOUNCING_BLADE(BouncingBlade.class),
	CHAIN_LIGHTNING(ChainLightning.class),
	CHARGE_BEAM(ChargeBeam.class),
	CR4PCANNON(CR4PCannon.class),
	FLOUNDERBUSS(Flounderbuss.class),
	FUGUN(Fugun.class),
	GRENADE_LAUNCHER(GrenadeLauncher.class),
	ICEBERG(Iceberg.class),
	IRON_BALL_LAUNCHER(IronBallLauncher.class),
	LASER_GUIDED_ROCKET(LaserGuidedRocket.class),
	LASER_RIFLE(LaserRifle.class),
	MACHINEGUN(Machinegun.class),
	MINIGUN(Minigun.class),
	MORAYGUN(Moraygun.class),
	NEMATOCYDEARM(Nematocydearm.class),
	POPPER(Popper.class),
	SCRAPRIPPER(Scrapripper.class),
	SLODGEGUN(SlodgeGun.class),
	SNIPER_RIFLE(SniperRifle.class),
	SPEARGUN(Speargun.class),
	STICKY_BOMB_LAUNCHER(StickyBombLauncher.class),
	STORMCALLER(Stormcaller.class),
	TESLA_COIL(TeslaCoil.class),
	TORPEDO_LAUNCHER(TorpedoLauncher.class),
	TRICK_GUN(TrickGun.class),
	UNDERMINER(Underminer.class),
	WAVE_CANNON(WaveCannon.class),
	
	NOTHING(NothingWeapon.class),
	
	;
	
	private Class<? extends Equipable> weapon;	
	private InfoItem info;
	
	UnlockEquip(Class<? extends Equipable> weapon) {
		this.weapon = weapon;
	}
	
	public static Array<UnlockEquip> getUnlocks(boolean unlock, UnlockTag... tags) {
		Array<UnlockEquip> items = new Array<UnlockEquip>();
		
		for (UnlockEquip u : UnlockEquip.values()) {
			
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

	public Class<? extends Equipable> getWeapon() {
		return weapon;
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
	
	public static UnlockEquip getUnlockFromEquip(Class<? extends Equipable> weapon) {
		for (UnlockEquip unlock: UnlockEquip.values()) {
			if (unlock.weapon.equals(weapon)) {
				return unlock;
			}
		}
		return null;
	}
}