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

	MAXIMILLIAN(Sprite.SpriteType.MAXIMILLIAN),
	MOREAU(Sprite.SpriteType.MOREAU),
	ROCLAIRE(Sprite.SpriteType.ROCLAIRE) {
		
		//custom wobble for bucket bobbing
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			if (!moving) {
				return 0;
			}
			if (grounded) {
				switch (frame) {
				case 0:
					return 0;
				case 1:
					return 1;
				case 2:
					return 2;
				case 3:
					return 3;
				case 4:
					return 4;
				case 5:
					return 5;
				case 6:
					return 3.5f;
				case 7:
					return 1;
				default:
					return 0;
				}
			} else {
				switch (frameHead) {
				case 0:
					return 0;
				case 1:
					return 0.5f;
				case 2:
					return 1;
				case 3:
					return 1.5f;
				case 4:
					return 2;
				case 5:
					return 1.5f;
				case 6:
					return 1;
				case 7:
					return 0.5f;
				default:
					return 0;
				}
			}
		}
		
	},
	TAKANORI(Sprite.SpriteType.TAKANORI),
	TELEMACHUS(Sprite.SpriteType.TELEMACHUS),
	WANDA(Sprite.SpriteType.WANDA) {
		
		//this just makes wanda's head offset slightly higher to compensate for lack of a neck
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			return getWobbleOffsetBody(frame, grounded, moving) + 5;
		}
	},
	MOREAU_FESTIVE(Sprite.SpriteType.MOREAU_FESTIVE),
	MOREAU_PARTY(Sprite.SpriteType.MOREAU_PARTY)
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
	
	public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
		return getWobbleOffsetBody(frame, grounded, moving);
	}
	
	public float getWobbleOffsetBody(int frame, boolean grounded, boolean moving) {
		if (grounded && moving) {
			switch (frame) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 4;
			case 5:
				return 3;
			case 6:
				return 3;
			case 7:
				return 1;
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public InfoItem getInfo() { return info; }

	public void setInfo(InfoItem info) { this.info = info; }

	public Sprite.SpriteType getSprite() { return sprite; }
}