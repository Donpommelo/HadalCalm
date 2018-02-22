package com.mygdx.hadal.equip;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;

public enum Unlock {
	
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

	;
	
	private Class<? extends Equipable> weapon;
	private boolean unlocked;
	private UnlockType type;
	
	Unlock(Class<? extends Equipable> weapon, UnlockType type) {
		this.weapon = weapon;
		this.type = type;
		this.unlocked = false;
	}
	
	public static Array<Class<? extends Equipable>> getUnlocks(UnlockType type) {
		Array<Class<? extends Equipable>> items = new Array<Class<? extends Equipable>>();
		
		for (Unlock u : Unlock.values()) {
			if (u.getType().equals(type) && u.isUnlocked()) {
				items.add(u.getWeapon());
			}
		}
		
		return items;
	}
	
	public static void retrieveUnlocks() {
		JsonReader json;
		JsonValue base;
		
		json = new JsonReader();
		base = json.parse(Gdx.files.internal("save/Unlocks.json"));
		
		for (JsonValue d : base) {
			valueOf(d.name()).setUnlocked(d.getBoolean("value"));
		}
	}
	
	public static void saveUnlocks() {		
		Gdx.files.local("save/Unlocks.json").writeString("", false);
		
		Json json = new Json();
		
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		
		for (Unlock u : Unlock.values()) {
			map.put(u.name(), u.unlocked);
		}
		
		Gdx.files.local("save/Unlocks.json").writeString(json.toJson(map), true);
	}
	
	public Class<? extends Equipable> getWeapon() {
		return weapon;
	}
	
	public UnlockType getType() {
		return type;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
	
	public enum UnlockType {
		RANGED,
		MELEE,
		MISC
	}
}


