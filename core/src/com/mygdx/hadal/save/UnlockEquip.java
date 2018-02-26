package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.utils.UnlocktoItem;

public enum UnlockEquip {
	
	BEEGUN(BeeGun.class, UnlockType.RANGED),
	BOILER(Boiler.class, UnlockType.RANGED),
	BOOMERANG(Boomerang.class, UnlockType.RANGED),
	BOUNCING_BLADE(BouncingBlade.class, UnlockType.RANGED),
	CHARGE_BEAM(ChargeBeam.class, UnlockType.RANGED),
	GRENADE_LAUNCHER(GrenadeLauncher.class, UnlockType.RANGED),
	IRON_BALL_LAUNCHER(IronBallLauncher.class, UnlockType.RANGED),
	LASER_GUIDED_ROCKET(LaserGuidedRocket.class, UnlockType.RANGED),
	MACHINEGUN(Machinegun.class, UnlockType.RANGED),
	NEMATOCYDEARM(Nematocydearm.class, UnlockType.RANGED),
	CR4PCANNON(Scattergun.class, UnlockType.RANGED),
	SLODGEGUN(SlodgeGun.class, UnlockType.RANGED),
	SPEARGUN(Speargun.class, UnlockType.RANGED),
	STICKY_BOMB_LAUNCHER(StickyBombLauncher.class, UnlockType.RANGED),
	STORMCALLER(Stormcaller.class, UnlockType.RANGED),
	TORPEDO_LAUNCHER(TorpedoLauncher.class, UnlockType.RANGED),
	
	SCRAPRIPPER(Scrapripper.class, UnlockType.MELEE),
	
	MOMENTUM_SHOOTER(MomentumShooter.class, UnlockType.MISC),
	MELON(Melon.class, UnlockType.MISC),

	NOTHING(Nothing.class, UnlockType.MISC),
	
	;
	
	private Class<? extends Equipable> weapon;
	private Equipable singleton;
	private boolean unlocked;
	private UnlockType type;
	
	UnlockEquip(Class<? extends Equipable> weapon, UnlockType type) {
		this.weapon = weapon;
		this.type = type;
		this.unlocked = true;
		this.singleton = UnlocktoItem.getUnlock(this, null);
	}
	
	public static Array<UnlockEquip> getUnlocks(UnlockType type) {
		Array<UnlockEquip> items = new Array<UnlockEquip>();
		
		for (UnlockEquip u : UnlockEquip.values()) {
			if ((u.getType().equals(type) || UnlockType.ALL.equals(type)) && u.isUnlocked()) {
				items.add(u);
			}
		}
		
		return items;
	}

	public Class<? extends Equipable> getWeapon() {
		return weapon;
	}
	
	public UnlockType getType() {
		return type;
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
	}
	
	public enum UnlockType {
		ALL,
		RANGED,
		MELEE,
		MISC
	}
}


