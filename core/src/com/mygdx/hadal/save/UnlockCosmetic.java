package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;

import java.util.HashMap;

import static com.mygdx.hadal.effects.PlayerSpriteHelper.gibDuration;
import static com.mygdx.hadal.effects.PlayerSpriteHelper.gibGravity;

/**
 * An UnlockCosmetic represents a single cosmetic item like a hat. Each cosmetic item contains a list of characters that
 * can equip it (each character - cosmetic relationship is represented by 1 CharacterCosmetic)
 * @author Cikola Cluthurlando
 */
public enum UnlockCosmetic {

    NOTHING_HAT1(CosmeticSlot.HAT1),
    NOTHING_HAT2(CosmeticSlot.HAT2),
    NOTHING_EYE(CosmeticSlot.EYE),
    NOTHING_NOSE(CosmeticSlot.NOSE),
    NOTHING_MOUTH(CosmeticSlot.MOUTH),
    NOTHING_HEAD(CosmeticSlot.HEAD),

    EYEPATCH(CosmeticSlot.EYE, GameText.EYEPATCH, GameText.EYEPATCH_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_eyepatch"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_eyepatch"),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_eyepatch"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_eyepatch"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_eyepatch"),
            new CharacterCosmetic(UnlockCharacter.MAXIMILLIAN, "maximillian_eyepatch")),
    SNORKEL(CosmeticSlot.EYE, GameText.SNORKEL, GameText.SNORKEL_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_snorkel"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_snorkel"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_snorkel")),

    BICORNE(CosmeticSlot.HAT1, GameText.BICORNE, GameText.BICORNE_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_bicorne"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_bicorne"),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_bicorne"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_bicorne"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_bicorne")),
    FESTIVE_HAT(CosmeticSlot.HAT1, GameText.FESTIVE_HAT, GameText.FESTIVE_HAT_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_festive")),
    FISH_FEAR_ME_HAT(CosmeticSlot.HAT1, GameText.FISH_FEAR_ME_HAT, GameText.FISH_FEAR_ME_HAT_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_fear", "moreau_fear_mirror"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_fear", "takanori_fear_mirror"),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_fear", "telemachus_fear_mirror")),
    PARTY_HAT(CosmeticSlot.HAT1, GameText.PARTY_HAT, GameText.PARTY_HAT_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_party"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_party"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_party"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_party")),
    PROPELLER_BEANIE(CosmeticSlot.HAT1, GameText.PROPELLER_BEANIE, GameText.PROPELLER_BEANIE_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_propeller"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_propellor"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_propellor"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_propellor")),

    N95_MASK(CosmeticSlot.MOUTH, GameText.N95_MASK, GameText.N95_MASK_DESC, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_n95"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_n95"),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_n95"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_n95"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_n95"),
            new CharacterCosmetic(UnlockCharacter.MAXIMILLIAN, "maximillian_n95")),
    NOISEMAKER(CosmeticSlot.MOUTH, GameText.NOISEMAKER, GameText.NOISEMAKER_DESC, true, PlayMode.LOOP_PINGPONG,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_noisemaker").setOffsetX(-67.8f).setOffsetY(0),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_noisemaker").setOffsetX(-85.2f).setOffsetY(-3.0f),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_noisemaker").setOffsetX(-58.4f).setOffsetY(12.6f),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_noisemaker").setOffsetX(-91.8f).setOffsetY(0),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_noisemaker").setOffsetX(-30.6f).setOffsetY(-40.2f)),

    LONG_FACE(CosmeticSlot.HEAD, GameText.LONG_FACE, GameText.LONG_FACE_DESC, true, 0.0f, 144.6f,
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_head_long")),
    ;

    private final GameText name, desc;
    private final Array<UnlockManager.UnlockTag> tags = new Array<>();

    //This is the slot that this cosmetic goes into
    private final CosmeticSlot cosmeticSlot;

    //does the cosmetic sprite ragdoll when the user is defeated?
    private final boolean ragdoll;

    private float xOffset, yOffset;

    //Blank indicates that the cosmetic will not be rendered, instead representing an empty slot
    private boolean blank;

    //this maps each compatible character to their version of the cosmetic item
    private final HashMap<UnlockCharacter, CharacterCosmetic> cosmetics = new HashMap<>();

    UnlockCosmetic(CosmeticSlot cosmeticSlot, GameText name, GameText desc, boolean ragdoll,
                   CharacterCosmetic... compatibleCharacters) {
        this(cosmeticSlot, name, desc, ragdoll, PlayMode.LOOP, compatibleCharacters);
    }

    UnlockCosmetic(CosmeticSlot cosmeticSlot, GameText name, GameText desc, boolean ragdoll, PlayMode mode,
                   CharacterCosmetic... compatibleCharacters) {
        this.cosmeticSlot = cosmeticSlot;
        this.name = name;
        this.desc = desc;
        this.ragdoll = ragdoll;
        for (CharacterCosmetic cosmetic : compatibleCharacters) {
            cosmetics.put(cosmetic.getCompatibleCharacter(), cosmetic);
            cosmetic.setPlayMode(mode);
        }
        this.tags.add(UnlockManager.UnlockTag.HABERDASHER);
    }

    UnlockCosmetic(CosmeticSlot cosmeticSlot) {
        this(cosmeticSlot, GameText.NOTHING, GameText.NOTHING, false);
        this.blank = true;
    }

    UnlockCosmetic(CosmeticSlot cosmeticSlot, GameText name, GameText desc, boolean ragdoll, float xOffset, float yOffset,
                   CharacterCosmetic... compatibleCharacters) {
        this(cosmeticSlot, name, desc, ragdoll, compatibleCharacters);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * This is called when a player is rendered to render the cosmetic.
     * We find the character's version of the cosmetic and render it if existent
     */
    public void render(Batch batch, UnlockCharacter character, float animationTimeExtra, float scale, boolean flip, float locationX, float locationY) {
        if (!blank) {
            CharacterCosmetic cosmetic = cosmetics.get(character);
            if (cosmetic != null) {
                cosmetic.render(batch, animationTimeExtra, scale, flip, locationX - xOffset * scale, locationY - yOffset * scale);
            }
        }
    }

    /**
     * This is called when a player is ragdolled.
     * @return the cosmetic ragdoll
     */
    public Ragdoll createRagdoll(UnlockCharacter character, PlayState state, Vector2 playerLocation, float scale, Vector2 playerVelocity) {
        if (ragdoll) {
            CharacterCosmetic cosmetic = cosmetics.get(character);
            if (cosmetic != null) {
                return cosmetic.createRagdoll(state, playerLocation, scale, playerVelocity);
            }
        }
        return null;
    }

    /**
     * This checks if a character can wear this cosmetic or not
     */
    public boolean checkCompatibleCharacters(UnlockCharacter character) {
        return !cosmetics.containsKey(character);
    }

    /**
     * This acquires a list of all unlocked cosmetics (if unlock is true. otherwise just return all cosmetics that satisfy the tags)
     */
    public static Array<UnlockCosmetic> getUnlocks(PlayState state, boolean unlock, Array<UnlockManager.UnlockTag> tags) {
        Array<UnlockCosmetic> items = new Array<>();

        for (UnlockCosmetic u : UnlockCosmetic.values()) {

            boolean get = UnlockManager.checkTags(u.tags, tags);

            if (unlock && !UnlockManager.checkUnlock(state, UnlockManager.UnlockType.CHARACTER, u.toString())) {
                get = false;
            }

            if (get) {
                items.add(u);
            }
        }
        return items;
    }

    public CosmeticSlot getCosmeticSlot() { return cosmeticSlot; }

    public boolean isBlank() { return blank; }

    public String getName() { return name.text(); }

    public String getDesc() { return desc.text(); }

    public float getXOffset() { return xOffset; }

    public float getYOffset() { return yOffset; }

    private static final ObjectMap<String, UnlockCosmetic> UnlocksByName = new ObjectMap<>();
    static {
        for (UnlockCosmetic u : UnlockCosmetic.values()) {
            UnlocksByName.put(u.toString(), u);
        }
    }
    public static UnlockCosmetic getByName(String s) {
        return UnlocksByName.get(s, NOTHING_HAT1);
    }
}

/**
 * A CharacterCosmetic represents a single Character-Cosmetic relationship
 */
class CharacterCosmetic {

    public static final float cosmeticAnimationSpeed = 0.05f;

    //The id of the sprite in thte cosmetic texture atlas
    private final String spriteId;

    //the character that this cosmetic is worn by
    private final UnlockCharacter compatibleCharacter;

    //frames of sprites for rendering this cosmetic
    private Animation<TextureRegion> frames, framesMirror;

    //dimensions of the cosmetic
    private float cosmeticWidth, cosmeticHeight, offsetX, offsetY;

    //mirror indicates that a cosmetic is drawn with a different sprite when mirrored instead of just being flipped
    //this is used to properly render things like text
    private boolean mirror;
    private String spriteIdMirror;

    private PlayMode mode = Animation.PlayMode.LOOP;

    public CharacterCosmetic(UnlockCharacter compatibleCharacter, String spriteId) {
        this.compatibleCharacter = compatibleCharacter;
        this.spriteId = spriteId;
    }

    public CharacterCosmetic(UnlockCharacter compatibleCharacter, String spriteId, String spriteIdMirror) {
        this(compatibleCharacter, spriteId);
        this.spriteIdMirror = spriteIdMirror;
        this.mirror = true;
    }

    /**
     * This is called before rendering to retrieve the frames for the cosmetic sprite
     */
    public void getFrames() {
        if (frames == null) {
            frames = new Animation<>(cosmeticAnimationSpeed, ((TextureAtlas) HadalGame.assetManager.get(AssetList.COSMETICS_ATL.toString())).findRegions(spriteId));
            frames.setPlayMode(mode);
            if (frames.getKeyFrames().length != 0) {
                cosmeticWidth = frames.getKeyFrame(0).getRegionWidth();
                cosmeticHeight = frames.getKeyFrame(0).getRegionHeight();
            }
        }
        if (mirror && framesMirror == null) {
            framesMirror = new Animation<>(cosmeticAnimationSpeed, ((TextureAtlas) HadalGame.assetManager.get(AssetList.COSMETICS_ATL.toString())).findRegions(spriteIdMirror));
        }
    }

    /**
     * Draw the cosmetic at the correct location
     */
    public void render(Batch batch, float animationTimeExtra, float scale, boolean flip, float locationX, float locationY) {

        //mirrored sprites are drawn differently when character is flipped
        if (mirror && flip) {
            if (framesMirror == null) { getFrames(); }
            batch.draw(framesMirror.getKeyFrame(animationTimeExtra, true), locationX - cosmeticWidth * scale + offsetX * scale,
                    locationY + offsetY * scale,0, 0, cosmeticWidth * scale, cosmeticHeight * scale, 1, 1, 0);
        } else {
            if (frames == null) { getFrames(); }
            batch.draw(frames.getKeyFrame(animationTimeExtra, true), locationX + (flip ? -1 : 1) * offsetX * scale,
                    locationY + offsetY * scale,0, 0,(flip ? -1 : 1) * cosmeticWidth * scale,
                    cosmeticHeight * scale, 1, 1, 0);
        }
    }

    /**
     * Called when player ragdolls are created to spawn a ragdoll for the cosmetic if applicable
     */
    public Ragdoll createRagdoll(PlayState state, Vector2 playerLocation, float scale, Vector2 playerVelocity) {
        if (frames == null) { getFrames(); }
        if (frames.getKeyFrames().length != 0) {
            return new Ragdoll(state, playerLocation, new Vector2(cosmeticWidth, cosmeticHeight).scl(scale),
                    frames.getKeyFrame(0), playerVelocity, gibDuration, gibGravity, true, false, true);
        }
        return null;
    }

    public CharacterCosmetic setOffsetX(float offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public CharacterCosmetic setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public void setPlayMode(PlayMode mode) { this.mode = mode; }

    public UnlockCharacter getCompatibleCharacter() { return compatibleCharacter; }
}