package com.mygdx.hadal.equip;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.artifacts.*;

public enum UnlockArtifact {
	
	ANARCHISTS_COOKBOOK(AnarchistsCookbook.class),
	EELSKIN_COVER(EelskinCover.class),
	FRACTURE_PLATE(FracturePlate.class),
	GLUTTONOUS_GREY_GLOVE(GluttonousGreyGlove.class),
	GOOD_HEALTH(GoodHealth.class),
	LOAMSKIN_TALISMAN(LoamskinTalisman.class),
	MOON_FLUTHER(MoonFluther.class),
	NICE_SHOES(NiceShoes.class),
	NUCLEAR_PUNCH_THRUSTERS(NuclearPunchThrusters.class),
	RECKLESS_MARK(RecklessMark.class),
	RING_OF_THE_LAMPREY(RingoftheLamprey.class),
	ROOT_BOOTS(RootBoots.class),
	SKATE_WINGS(SkateWings.class),
	THROBBING_RAGE_GLAND(ThrobbingRageGland.class),
	TRIGGER_FINGER(TriggerFinger.class),
	VOID_HYPONOME(VoidHyponome.class),
	;
	
	private Class<? extends Artifact> artifact;
	private boolean unlocked;
	
	UnlockArtifact(Class<? extends Artifact> artifact) {
		this.artifact = artifact;
		this.unlocked = false;
	}
	
	public static Array<Class<? extends Artifact>> getUnlocks() {
		Array<Class<? extends Artifact>> items = new Array<Class<? extends Artifact>>();
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
			items.add(u.getArtifact());
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
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
			map.put(u.name(), u.unlocked);
		}
		
		Gdx.files.local("save/Unlocks.json").writeString(json.toJson(map), true);
	}
	
	public Class<? extends Artifact> getArtifact() {
		return artifact;
	}
	
	public String getName() {
		return name();
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
	
}


