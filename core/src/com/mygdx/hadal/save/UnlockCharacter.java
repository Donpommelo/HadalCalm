package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

/**
 * An UnlockCharacter represents a single playable character in the game
 * @author Lorlov Leston
 */
public enum UnlockCharacter {

	MAXIMILLIAN(AssetList.PLAYER_MAXIMILLIAN_ATL.toString(), AssetList.PLAYER_MAXIMILLIAN.toString(),
		Sprite.MAXIMILLIAN_SLUG, Sprite.MAXIMILLIAN_BUFF, 117.0f, 86.4f, 177.0f, 502.2f,
		0.96f, 0.31f,0.31f, 0.73f, 0.15f, 0.15f),
	MOREAU(AssetList.PLAYER_MOREAU_ATL.toString(), AssetList.PLAYER_MOREAU.toString(),
		Sprite.MOREAU_SLUG, Sprite.MOREAU_BUFF, 106.8f, 76.2f, 161.4f, 496.2f,
		.15f, 0.39f,0.10f, 0.57f, 0.68f,0.58f),
	ROCLAIRE(AssetList.PLAYER_ROCLAIRE_ATL.toString(), AssetList.PLAYER_ROCLAIRE.toString(),
		Sprite.ROCLAIRE_SLUG, Sprite.ROCLAIRE_BUFF, 114.0f, 88.8f, 168.6f, 509.4f,
		0.83f, 0.85f,0.89f, 0.83f, 0.85f,0.89f) {
		
		//custom wobble for bucket bobbing
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			if (!moving) {
				return 0;
			}
			if (grounded) {
				return switch (frame) {
					case 1, 7 -> 6.7f;
					case 2 -> 13.3f;
					case 3 -> 20.0f;
					case 4 -> 26.7f;
					case 5 -> 33.3f;
					case 6 -> 23.3f;
					default -> 0;
				};
			} else {
				return switch (frameHead) {
					case 1, 7 -> 3.3f;
					case 2, 6 -> 6.7f;
					case 3, 5 -> 10.0f;
					case 4 -> 13.3f;
					default -> 0;
				};
			}
		}
		
	},
	TAKANORI(AssetList.PLAYER_TAKANORI_ATL.toString(), AssetList.PLAYER_TAKANORI.toString(),
		Sprite.TAKANORI_SLUG, Sprite.TAKANORI_BUFF, 109.2f, 87.9f, 164.4f, 504.6f,
		1.00f, 0.73f,0.29f, 1.00f, 0.73f,0.29f),
	TELEMACHUS(AssetList.PLAYER_TELEMACHUS_ATL.toString(), AssetList.PLAYER_TELEMACHUS.toString(),
		Sprite.TELEMACHUS_SLUG, Sprite.TELEMACHUS_BUFF, 109.2f, 88.2f, 157.2f, 508.8f,
		0.52f, 0.70f,0.79f, 0.52f, 0.70f,0.79f),
	WANDA(AssetList.PLAYER_WANDA_ATL.toString(), AssetList.PLAYER_WANDA.toString(),
		Sprite.WANDA_SLUG, Sprite.WANDA_BUFF, 102.6f, 117.6f, 161.4f, 528.6f,
		0.47f, 0.37f,0.53f, 0.47f, 0.37f,0.53f) {
		
		//this just makes wanda's head offset slightly higher to compensate for lack of a neck
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			return getWobbleOffsetBody(frame, grounded, moving) + 33.3f;
		}
	}
	;
	
	private final String atlas;
	private final String texture;

	//these sprites are used in the results screen to indicate a win or loss
	private final Sprite slugTexture, buffTexture;

	private InfoItem info;

	//these are the character's primary and secondary suit colors used for color replacement
	private final Vector3 color1RGB = new Vector3();
	private final Vector3 color1HSV = new Vector3();
	private final Vector3 color2HSV = new Vector3();

	private final Vector2 slugHatOffset = new Vector2();
	private final Vector2 buffHatOffset = new Vector2();

	UnlockCharacter(String atlas, String texture, Sprite slugTexture, Sprite buffTexture,
					float slugHatX, float slugHatY,	float buffHatX, float buffHatY,
					float r1, float g1, float b1, float r2, float g2, float b2) {
		this.atlas = atlas;
		this.texture = texture;
		this.slugTexture = slugTexture;
		this.buffTexture = buffTexture;
		this.slugHatOffset.set(slugHatX, slugHatY);
		this.buffHatOffset.set(buffHatX, buffHatY);

		this.color1RGB.set(r1, g1, b1);
		Vector3 color2RGB = new Vector3();
		color2RGB.set(r2, g2, b2);
		Color color1 = new Color(r1, g1, b1, 1.0f);
		Color color2 = new Color(r1, g1, b1, 1.0f);

		float[] hsvTemp = new float[3];
		hsvTemp = color1.toHsv(hsvTemp);
		this.color1HSV.set(hsvTemp[0] / 360, hsvTemp[1], hsvTemp[2]);
		hsvTemp = color2.toHsv(hsvTemp);
		this.color2HSV.set(hsvTemp[0] / 360, hsvTemp[1], hsvTemp[2]);
	}

	/**
	 * This acquires a list of all unlocked characters (if unlock is true. otherwise just return all characters that satisfy the tags)
	 */
	public static Array<UnlockCharacter> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
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

	public static UnlockCharacter getRandCharFromPool(PlayState state) {
		Array<UnlockCharacter> unlocks = UnlockCharacter.getUnlocks(state, false, new Array<>());
		return unlocks.get(MathUtils.random(unlocks.size - 1));
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
			return switch (frame) {
				case 1, 7 -> 6.7f;
				case 2 -> 13.3f;
				case 3, 5, 6 -> 20.0f;
				case 4 -> 26.7f;
				default -> 0;
			};
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

	public Vector2 getSlugHatOffset() { return slugHatOffset; }

	public Vector2 getBuffHatOffset() {	return buffHatOffset; }

	public Vector3 getColor1RGB() { return color1RGB; }

	public Vector3 getColor1HSV() {	return color1HSV; }

	public Vector3 getColor2HSV() {	return color2HSV; }

	private static final ObjectMap<String, UnlockCharacter> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockCharacter u: UnlockCharacter.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockCharacter getByName(String s) {
		return UnlocksByName.get(s, MOREAU);
	}
}