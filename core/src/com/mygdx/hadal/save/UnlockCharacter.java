package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An UnlockCharacter represents a single playable character in the game
 * @author Lorlov Leston
 */
public enum UnlockCharacter {

	MAXIMILLIAN(AssetList.PLAYER_MAXIMILLIAN_ATL.toString(), AssetList.PLAYER_MAXIMILLIAN.toString(),
		Sprite.MAXIMILLIAN_SLUG, Sprite.MAXIMILLIAN_BUFF,
		0.96f, 0.31f,0.31f, 0.73f, 0.15f, 0.15f),
	MOREAU(AssetList.PLAYER_MOREAU_ATL.toString(), AssetList.PLAYER_MOREAU.toString(),
		Sprite.MOREAU_SLUG, Sprite.MOREAU_BUFF,
		.15f, 0.39f,0.10f, 0.57f, 0.68f,0.58f),
	ROCLAIRE(AssetList.PLAYER_ROCLAIRE_ATL.toString(), AssetList.PLAYER_ROCLAIRE.toString(),
		Sprite.ROCLAIRE_SLUG, Sprite.ROCLAIRE_BUFF,
		0.83f, 0.85f,0.89f, 0.83f, 0.85f,0.89f) {
		
		//custom wobble for bucket bobbing
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			if (!moving) {
				return 0;
			}
			if (grounded) {
				switch (frame) {
				case 1:
				case 7:
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
				default:
					return 0;
				}
			} else {
				switch (frameHead) {
				case 1:
				case 7:
					return 0.5f;
				case 2:
				case 6:
					return 1;
				case 3:
				case 5:
					return 1.5f;
				case 4:
					return 2;
				default:
					return 0;
				}
			}
		}
		
	},
	TAKANORI(AssetList.PLAYER_TAKANORI_ATL.toString(), AssetList.PLAYER_TAKANORI.toString(),
		Sprite.TAKANORI_SLUG, Sprite.TAKANORI_BUFF,
		1.00f, 0.73f,0.29f, 1.00f, 0.73f,0.29f),
	TELEMACHUS(AssetList.PLAYER_TELEMACHUS_ATL.toString(), AssetList.PLAYER_TELEMACHUS.toString(),
		Sprite.TELEMACHUS_SLUG, Sprite.TELEMACHUS_BUFF,
		0.52f, 0.70f,0.79f, 0.52f, 0.70f,0.79f),
	WANDA(AssetList.PLAYER_WANDA_ATL.toString(), AssetList.PLAYER_WANDA.toString(),
		Sprite.WANDA_SLUG, Sprite.WANDA_BUFF,
		0.47f, 0.37f,0.53f, 0.47f, 0.37f,0.53f) {
		
		//this just makes wanda's head offset slightly higher to compensate for lack of a neck
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			return getWobbleOffsetBody(frame, grounded, moving) + 5;
		}
	},
	MOREAU_FESTIVE(AssetList.PLAYER_MOREAU_FESTIVE_ATL.toString(), AssetList.PLAYER_MOREAU.toString(),
		Sprite.MOREAU_FESTIVE_SLUG, Sprite.MOREAU_FESTIVE_BUFF,
		.15f, 0.39f,0.10f, 0.57f, 0.68f,0.58f),
	MOREAU_PARTY(AssetList.PLAYER_MOREAU_PARTY_ATL.toString(), AssetList.PLAYER_MOREAU.toString(),
		Sprite.MOREAU_PARTY_SLUG, Sprite.MOREAU_PARTY_BUFF,
		.15f, 0.39f,0.10f, 0.57f, 0.68f,0.58f)
	;
	
	private final String atlas;
	private final String texture;

	//these sprites are used i nthe results screen to indicate a win or loss
	private final Sprite slugTexture, buffTexture;

	private InfoItem info;

	//these are the character's primary and secondary suit colors used for color replacement
	private final Vector3 color1= new Vector3();
	private final Vector3 color2 = new Vector3();

	UnlockCharacter(String atlas, String texture, Sprite slugTexture, Sprite buffTexture,
					float r1, float g1, float b1, float r2, float g2, float b2) {
		this.atlas = atlas;
		this.texture = texture;
		this.slugTexture = slugTexture;
		this.buffTexture = buffTexture;
		this.color1.set(r1, g1, b1);
		this.color2.set(r2, g2, b2);
	}

	/**
	 * This acquires a list of all unlocked characters (if unlock is true. otherwise just return all characters that satisfy the tags)
	 */
	public static Array<UnlockCharacter> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockCharacter> items = new Array<>();
		
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

	/**
	 * This returns the amount of head wobble. Override this if head wobble is desynchronized from body.
	 */
	public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
		return getWobbleOffsetBody(frame, grounded, moving);
	}

	/**
	 * This returns the amount of body wobble depending on the player frame and movement state
	 */
	public float getWobbleOffsetBody(int frame, boolean grounded, boolean moving) {
		if (grounded && moving) {
			switch (frame) {
			case 1:
			case 7:
				return 1;
			case 2:
				return 2;
			case 3:
			case 5:
			case 6:
				return 3;
			case 4:
				return 4;
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public InfoItem getInfo() { return info; }

	public void setInfo(InfoItem info) { this.info = info; }

	public TextureAtlas getAtlas() { return HadalGame.assetManager.get(atlas); }

	public Texture getTexture() { return HadalGame.assetManager.get(texture); }

	public Sprite getSlugSprite() { return slugTexture; }

	public Sprite getBuffSprite() { return buffTexture; }

	public Vector3 getColor1() { return color1; }

	public Vector3 getColor2() { return color2; }

	private static final HashMap<String, UnlockCharacter> UnlocksByName = new HashMap<>();
	static {
		for (UnlockCharacter u: UnlockCharacter.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockCharacter getByName(String s) {
		return UnlocksByName.getOrDefault(s, MOREAU);
	}
}