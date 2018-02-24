package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.managers.AssetList;

public enum UnlockCharacter {

	MOREAU(AssetList.PLAYER_MOREAU_ATL.toString(), "Moreau"),
	TELEMACHUS(AssetList.PLAYER_TELE_ATL.toString(), "Telemachus"),
	TAKANORI(AssetList.PLAYER_TAKA_ATL.toString(), "Takanori"),
	;
	private String sprite, name;
	private boolean unlocked;
	
	UnlockCharacter(String sprite, String name) {
		this.sprite = sprite;
		this.name= name;
		this.unlocked = false;
	}

	public static Array<UnlockCharacter> getUnlocks() {
		Array<UnlockCharacter> items = new Array<UnlockCharacter>();
		
		for (UnlockCharacter u : UnlockCharacter.values()) {
			if (u.isUnlocked()) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public String getSprite() {
		return sprite;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
}
