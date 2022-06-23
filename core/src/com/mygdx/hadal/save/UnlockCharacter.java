package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.ColorPalette;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;

/**
 * An UnlockCharacter represents a single playable character in the game
 * @author Lorlov Leston
 */
public enum UnlockCharacter {

	MAXIMILLIAN(AssetList.PLAYER_MAXIMILLIAN_ATL.toString(), AssetList.PLAYER_MAXIMILLIAN.toString(),
			GameText.MAXIMILLIAN, GameText.MAXIMILLIAN_DESC,
			Sprite.MAXIMILLIAN_SLUG, Sprite.MAXIMILLIAN_BUFF, ColorPalette.MAXIMILLIAN,117.0f, 86.4f, 177.0f, 502.2f),

	MOREAU(AssetList.PLAYER_MOREAU_ATL.toString(), AssetList.PLAYER_MOREAU.toString(),
			GameText.MOREAU, GameText.MOREAU_DESC,
			Sprite.MOREAU_SLUG,	Sprite.MOREAU_BUFF, ColorPalette.MOREAU, 106.8f, 76.2f, 161.4f, 496.2f),

	ROCLAIRE(AssetList.PLAYER_ROCLAIRE_ATL.toString(), AssetList.PLAYER_ROCLAIRE.toString(),
			GameText.ROCLAIRE, GameText.ROCLAIRE_DESC,
			Sprite.ROCLAIRE_SLUG, Sprite.ROCLAIRE_BUFF, ColorPalette.ROCLAIRE, 114.0f, 88.8f, 168.6f, 509.4f) {
		
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
			GameText.TAKANORI, GameText.TAKANORI_DESC,
			Sprite.TAKANORI_SLUG, Sprite.TAKANORI_BUFF, ColorPalette.TAKANORI, 109.2f, 87.9f, 164.4f, 504.6f),

	TELEMACHUS(AssetList.PLAYER_TELEMACHUS_ATL.toString(), AssetList.PLAYER_TELEMACHUS.toString(),
			GameText.TELEMACHUS, GameText.TELEMACHUS_DESC,
			Sprite.TELEMACHUS_SLUG, Sprite.TELEMACHUS_BUFF, ColorPalette.TELEMACHUS, 109.2f, 88.2f, 157.2f, 508.8f),

	WANDA(AssetList.PLAYER_WANDA_ATL.toString(), AssetList.PLAYER_WANDA.toString(),
			GameText.WANDA, GameText.WANDA_DESC,
			Sprite.WANDA_SLUG, Sprite.WANDA_BUFF, ColorPalette.WANDA, 102.6f, 117.6f, 161.4f, 528.6f) {
		
		//this just makes wanda's head offset slightly higher to compensate for lack of a neck
		@Override
		public float getWobbleOffsetHead(int frame, int frameHead, boolean grounded, boolean moving) {
			return getWobbleOffsetBody(frame, grounded, moving) + 33.3f;
		}
	}
	;
	
	private final String atlas;
	private final String texture;

	private final GameText name, desc;
	private final Array<UnlockTag> tags = new Array<>();

	//these sprites are used in the results screen to indicate a win or loss
	private final Sprite slugTexture, buffTexture;

	//these are the character's primary and secondary suit colors used for color replacement
	private final ColorPalette palette;

	private final Vector2 slugHatOffset = new Vector2();
	private final Vector2 buffHatOffset = new Vector2();

	UnlockCharacter(String atlas, String texture, GameText name, GameText desc,
					Sprite slugTexture, Sprite buffTexture, ColorPalette palette,
					float slugHatX, float slugHatY,	float buffHatX, float buffHatY, UnlockTag... tags) {
		this.name = name;
		this.desc = desc;
		this.atlas = atlas;
		this.texture = texture;
		this.slugTexture = slugTexture;
		this.buffTexture = buffTexture;
		this.palette = palette;
		this.slugHatOffset.set(slugHatX, slugHatY);
		this.buffHatOffset.set(buffHatX, buffHatY);

		this.tags.add(UnlockTag.DORMITORY);
		this.tags.addAll(tags);
	}

	/**
	 * This acquires a list of all unlocked characters (if unlock is true. otherwise just return all characters that satisfy the tags)
	 */
	public static Array<UnlockCharacter> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
		Array<UnlockCharacter> items = new Array<>();
		
		for (UnlockCharacter u : UnlockCharacter.values()) {
			
			boolean get = UnlockManager.checkTags(u.tags, tags);
			
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

	public String getName() { return name.text(); }

	public String getDesc() { return desc.text(); }

	public TextureAtlas getAtlas() { return HadalGame.assetManager.get(atlas); }

	public Texture getTexture() { return HadalGame.assetManager.get(texture); }

	public Sprite getSlugSprite() { return slugTexture; }

	public Sprite getBuffSprite() { return buffTexture; }

	public ColorPalette getPalette() { return palette; }

	public Vector2 getSlugHatOffset() { return slugHatOffset; }

	public Vector2 getBuffHatOffset() {	return buffHatOffset; }

	private static final ObjectMap<String, UnlockCharacter> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockCharacter u : UnlockCharacter.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockCharacter getByName(String s) {
		return UnlocksByName.get(s, MOREAU);
	}
}