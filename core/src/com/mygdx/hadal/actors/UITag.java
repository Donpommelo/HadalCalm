package com.mygdx.hadal.actors;

public class UITag {

	private uiType type;
	private String misc;
	private float scale;
	
	private static final float defScale = 0.4f;
	
	public UITag(uiType type, String misc, float scale) {
		this.type = type;
		this.misc = misc;
		this.scale = scale;
	}
	
	public UITag(uiType type) {
		this(type, "", defScale);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!UITag.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final UITag other = (UITag) obj;
	    
	    if (!getType().equals(other.getType()) || !getMisc().equals(other.getMisc()) ) {
			return false;
	    }
	    
		return true;
	}
	
	public uiType getType() {
		return type;
	}

	public void setType(uiType type) {
		this.type = type;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public enum uiType {
		SCORE,
		HISCORE,
		SCRAP,
		SCRIP,
		LIVES,
		TIMER,
		MISC,	
	}
}
