package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.utils.UnlocktoItem;
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

	NOTHING(Nothing.class),
	;
	
	private Class<? extends Artifact> artifact;
	private Artifact singleton;
	private boolean unlocked;
	
	UnlockArtifact(Class<? extends Artifact> artifact) {
		this.artifact = artifact;
		this.unlocked = false;
		this.singleton = UnlocktoItem.getUnlock(this);
	}
	
	public static Array<UnlockArtifact> getUnlocks() {
		Array<UnlockArtifact> items = new Array<UnlockArtifact>();
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
			if (u.isUnlocked()) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public Class<? extends Artifact> getArtifact() {
		return artifact;
	}
	
	public String getName() {
		return singleton.getName();
	}
	
	public String getDescr() {
		return singleton.getDescr();
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
	
}


