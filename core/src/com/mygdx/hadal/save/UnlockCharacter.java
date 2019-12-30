package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.save.UnlockManager.UnlockType;

public enum UnlockCharacter {

	MOREAU(Sprite.SpriteType.MOREAU),
	TELEMACHUS(Sprite.SpriteType.TELEMACHUS),
	TAKANORI(Sprite.SpriteType.TAKANORI),
	MOREAU_FESTIVE(Sprite.SpriteType.MOREAU_FESTIVE)
	;
	
	private Sprite.SpriteType sprite;
	private InfoItem info;
	
	UnlockCharacter(Sprite.SpriteType sprite) {
		this.sprite = sprite;
	}

	public static Array<UnlockCharacter> getUnlocks(Record record) {
		Array<UnlockCharacter> items = new Array<UnlockCharacter>();
		
		for (UnlockCharacter u : UnlockCharacter.values()) {
			if (UnlockManager.checkUnlock(record, UnlockType.CHARACTER, u.toString())) {
				items.add(u);
			}
		}
		return items;
	}
	
	public InfoItem getInfo() { return info; }

	public void setInfo(InfoItem info) { this.info = info; }

	public Sprite.SpriteType getSprite() { return sprite; }
}