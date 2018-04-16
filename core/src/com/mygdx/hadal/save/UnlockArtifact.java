package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.equip.artifacts.*;

public enum UnlockArtifact {
	
	ANARCHISTS_COOKBOOK(AnarchistsCookbook.class),
	BLOODY_LUST(Bloodylust.class),
	CONFIDENCE(Confidence.class),
	EELSKIN_COVER(EelskinCover.class),
	ENVENOMED_EARTH(EnvenomedEarth.class),
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
	SCALING_SCALES(ScalingScales.class),
	SKATE_WINGS(SkateWings.class),
	THROBBING_RAGE_GLAND(ThrobbingRageGland.class),
	TRIGGER_FINGER(TriggerFinger.class),
	VOID_HYPONOME(VoidHyponome.class),

	NOTHING(Nothing.class),
	;
	
	private Class<? extends Artifact> artifact;
	private InfoArtifact info;
	
	UnlockArtifact(Class<? extends Artifact> artifact) {
		this.artifact = artifact;
	}
	
	public static Array<UnlockArtifact> getUnlocks(boolean unlock, UnlockTag... tags) {
		Array<UnlockArtifact> items = new Array<UnlockArtifact>();
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
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
	
	public Class<? extends Artifact> getArtifact() {
		return artifact;
	}
	
	public InfoArtifact getInfo() {
		return info;
	}
	
	public void setInfo(InfoArtifact info) {
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
	
	public int getCost() {
		return info.getCost();
	}
	
	public void setUnlocked(boolean unlock) {
		info.setUnlocked(unlock);
	}
	
}


