package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

/**
 * An UnlockCharacter represents a single playable character in the game
 * @author Zachary Tu
 */
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

	/**
	 * This acquires a list of all unlocked characters (if unlock is true. otherwise just return all characters that satisfy the tags)
	 */
	public static Array<UnlockCharacter> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockCharacter> items = new Array<UnlockCharacter>();
		
		for (UnlockCharacter u : UnlockCharacter.values()) {
			
			boolean get = UnlockManager.checkTags(u.getInfo(), tags);
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.CHARACTER, u.toString())) {
				get = false;
			}
			
			if (get) {
				items.add(u);
			}
		}
		return items;
	}
	
	public InfoItem getInfo() { return info; }

	public void setInfo(InfoItem info) { this.info = info; }

	public Sprite.SpriteType getSprite() { return sprite; }
}