package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.utils.UnlocktoItem;

public enum UnlockEquip {
	
	BEEGUN(BeeGun.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	BOILER(Boiler.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	BOOMERANG(Boomerang.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	BOUNCING_BLADE(BouncingBlade.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	CHARGE_BEAM(ChargeBeam.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	GRENADE_LAUNCHER(GrenadeLauncher.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	ICEBERG(Iceberg.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	IRON_BALL_LAUNCHER(IronBallLauncher.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	LASER_GUIDED_ROCKET(LaserGuidedRocket.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	LASER_RIFLE(LaserRifle.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	MACHINEGUN(Machinegun.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	NEMATOCYDEARM(Nematocydearm.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	CR4PCANNON(Scattergun.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	SLODGEGUN(SlodgeGun.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	SPEARGUN(Speargun.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	STICKY_BOMB_LAUNCHER(StickyBombLauncher.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	STORMCALLER(Stormcaller.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	TELEKINESIS(TelekineticBlast.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	TORPEDO_LAUNCHER(TorpedoLauncher.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.RANGED),
	
	SCRAPRIPPER(Scrapripper.class, EquipTag.ARMORY, EquipTag.RANDOM_POOL, EquipTag.MELEE),
	
	MOMENTUM_SHOOTER(MomentumShooter.class, EquipTag.ARMORY, EquipTag.MISC),
	MELON(Melon.class, EquipTag.ARMORY, EquipTag.MISC),

	NOTHING(Nothing.class, EquipTag.MISC),
	
	;
	
	private Class<? extends Equipable> weapon;
	private Equipable singleton;
	private String descr = "<DESCR PLACEHOLDER>";
	private boolean unlocked;
	private EquipTag[] tags;
	
	UnlockEquip(Class<? extends Equipable> weapon, EquipTag... tags) {
		this.weapon = weapon;
		this.tags = tags;
		this.unlocked = true;
		this.singleton = UnlocktoItem.getUnlock(this, null);
	}
	
	public static Array<UnlockEquip> getUnlocks(boolean unlock, EquipTag... tags) {
		Array<UnlockEquip> items = new Array<UnlockEquip>();
		
		for (UnlockEquip u : UnlockEquip.values()) {
			
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getTags().length; j++) {
					if (tags[i].equals(u.getTags()[j])) {
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
	
	public EquipTag[] getTags() {
		return tags;
	}
	
	public Equipable getSingleton() {
		return singleton;
	}

	public String getName() {
		return singleton.getName();
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
		UnlockManager.saveUnlocks();
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	//TODO: implement weapon costs
	public int getCost() {
		return 10;
	}
	
	public enum EquipTag {
		ARMORY,
		RANDOM_POOL,
		RANGED,
		MELEE,
		MISC
	}
}


