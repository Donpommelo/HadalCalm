package com.mygdx.hadal.actors;

/**
 * This is not technically an actor but I don't know where else to put it. Maybe move it later
 * 
 * A UITag is anything that can show up in the UIExtra actor in the top corner o fthe screen. This can include information about 
 * score, lives, other info and can be dynamically changed within a level using the UIChanger event.
 * 
 * @author Zachary Tu
 *
 */
public class UITag {

	//The type of this tag. specifies what kind of information will be displayed.
	private uiType type;
	
	//If this tag is of the "MISC" type, this variable will hold the string to be displayed. Otherwise, this will be "".
	private String misc;
	
	public UITag(uiType type, String misc) {
		this.type = type;
		this.misc = misc;
	}
	
	/**
	 * Constructor for standard non-MISC tags that consist of only a type. "Default Tags"
	 * @param type: type of the new tag.
	 */
	public UITag(uiType type) {
		this(type, "");
	}

	/**
	 * Overrode equals method to allow for removing tags from the UI by only matching type and misc string.
	 */
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
	
	/**
	 * These are the various types of tags that can be added/removed from the UI.
	 * @author Zachary Tu
	 *
	 */
	public enum uiType {
		SCORE,
		HISCORE,
		WINS,
		SCRAP,
		LIVES,
		TIMER,
		MISC,
		EMPTY
	}
}
