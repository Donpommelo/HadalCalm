package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.Sprite;

public enum UnlockCharacter {

	MOREAU(Sprite.SpriteType.MOREAU),
	TELEMACHUS(Sprite.SpriteType.TELEMACHUS),
	TAKANORI(Sprite.SpriteType.TAKANORI),
	MOREAU_FESTIVE(Sprite.SpriteType.MOREAU_FESTIVE)
	;
	
	private Sprite.SpriteType sprite;
	private InfoCharacter info;
	
	UnlockCharacter(Sprite.SpriteType sprite) {
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

	public Sprite.SpriteType getSprite() {
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
