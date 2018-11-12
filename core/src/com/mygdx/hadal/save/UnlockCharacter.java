package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.managers.AssetList;

public enum UnlockCharacter {

	MOREAU(AssetList.PLAYER_MOREAU_ATL.toString()),
	TELEMACHUS(AssetList.PLAYER_TELE_ATL.toString()),
	TAKANORI(AssetList.PLAYER_TAKA_ATL.toString()),
	;
	
	private String sprite;
	private InfoCharacter info;
	
	UnlockCharacter(String sprite) {
		this.sprite = sprite;
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
	
	public InfoCharacter getInfo() {
		return info;
	}

	public void setInfo(InfoCharacter info) {
		this.info = info;
	}

	public void setSprite(String sprite) {
		this.sprite = sprite;
	}

	public String getSprite() {
		return sprite;
	}
	
	public String getName() {
		return info.getName();
	}
	
	public String getBio() {
		return info.getBio();
	}
	
	public String getDescr() {
		return info.getDescription();
	}
	
	public String getDescrLong() {
		return info.getDescriptionLong();
	}
	
	public boolean isUnlocked() {
		return info.isUnlocked();
	}

	public void setUnlocked(boolean unlocked) {
		info.setUnlocked(unlocked);
	}
}
