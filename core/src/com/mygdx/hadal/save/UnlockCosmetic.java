package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.states.PlayState;

import java.util.HashMap;

import static com.mygdx.hadal.effects.PlayerSpriteHelper.gibDuration;
import static com.mygdx.hadal.effects.PlayerSpriteHelper.gibGravity;

public enum UnlockCosmetic {

    NOTHING_HAT1(CosmeticSlot.HAT1),
    NOTHING_HAT2(CosmeticSlot.HAT2),
    NOTHING_EYE(CosmeticSlot.EYE),
    NOTHING_NOSE(CosmeticSlot.NOSE),
    NOTHING_MOUTH(CosmeticSlot.MOUTH),
    FESTIVE_HAT(CosmeticSlot.HAT1, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_festive")),
    FISH_FEAR_ME_HAT(CosmeticSlot.HAT1, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_fear", "moreau_fear_mirror"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_fear", "takanori_fear_mirror"),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_fear", "telemachus_fear_mirror")),
    PARTY_HAT(CosmeticSlot.HAT1, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_party"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_party"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_party"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_party")),

    N95_MASK(CosmeticSlot.MOUTH, true,
            new CharacterCosmetic(UnlockCharacter.MOREAU, "moreau_n95"),
            new CharacterCosmetic(UnlockCharacter.TAKANORI, "takanori_n95"),
            new CharacterCosmetic(UnlockCharacter.TELEMACHUS, "telemachus_n95"),
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_n95"),
            new CharacterCosmetic(UnlockCharacter.ROCLAIRE, "roclaire_n95"),
            new CharacterCosmetic(UnlockCharacter.MAXIMILLIAN, "maximillian_n95")),

    ;

    private InfoItem info;

    private final CosmeticSlot cosmeticSlot;
    private final boolean ragdoll;
    private boolean blank;

    private final HashMap<UnlockCharacter, CharacterCosmetic> cosmetics = new HashMap<>();

    UnlockCosmetic(CosmeticSlot cosmeticSlot, boolean ragdoll, CharacterCosmetic... compatibleCharacters) {
        this.cosmeticSlot = cosmeticSlot;
        this.ragdoll = ragdoll;
        for (CharacterCosmetic cosmetic: compatibleCharacters) {
            cosmetics.put(cosmetic.getCompatibleCharacter(), cosmetic);
        }
    }

    UnlockCosmetic(CosmeticSlot cosmeticSlot) {
        this(cosmeticSlot, false);
        this.blank = true;
    }

    public void render(SpriteBatch batch, UnlockCharacter character, float animationTimeExtra, float scale, boolean flip, float locationX, float locationY) {
        if (!blank) {
            CharacterCosmetic cosmetic = cosmetics.get(character);
            if (cosmetic != null) {
                cosmetic.render(batch, animationTimeExtra, scale, flip, locationX, locationY);
            }
        }
    }

    public Ragdoll createRagdoll(UnlockCharacter character, PlayState state, Vector2 playerLocation, float scale, Vector2 playerVelocity) {
        if (ragdoll) {
            CharacterCosmetic cosmetic = cosmetics.get(character);
            if (cosmetic != null) {
                return cosmetic.createRagdoll(state, playerLocation, scale, playerVelocity);
            }
        }
        return null;
    }

    public boolean checkCompatibleCharacters(UnlockCharacter character) {
        return !cosmetics.containsKey(character);
    }

    /**
     * This acquires a list of all unlocked characters (if unlock is true. otherwise just return all characters that satisfy the tags)
     */
    public static Array<UnlockCosmetic> getUnlocks(PlayState state, boolean unlock, Array<UnlockManager.UnlockTag> tags) {
        Array<UnlockCosmetic> items = new Array<>();

        for (UnlockCosmetic u : UnlockCosmetic.values()) {

            boolean get = UnlockManager.checkTags(u.getInfo(), tags);

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

    public InfoItem getInfo() { return info; }

    public void setInfo(InfoItem info) { this.info = info; }

    private static final ObjectMap<String, UnlockCosmetic> UnlocksByName = new ObjectMap<>();
    static {
        for (UnlockCosmetic u: UnlockCosmetic.values()) {
            UnlocksByName.put(u.toString(), u);
        }
    }
    public static UnlockCosmetic getByName(String s) {
        return UnlocksByName.get(s, NOTHING_HAT1);
    }
}

class CharacterCosmetic {

    private boolean mirror;
    private final String spriteId;
    private final UnlockCharacter compatibleCharacter;
    private String spriteIdMirror;
    private Animation<TextureRegion> frames, framesMirror;
    private float cosmeticWidth, cosmeticHeight;

    private static final float animationSpeed = 0.2f;

    public CharacterCosmetic(UnlockCharacter compatibleCharacter, String spriteId) {
        this.compatibleCharacter = compatibleCharacter;
        this.spriteId = spriteId;
    }

    public CharacterCosmetic(UnlockCharacter compatibleCharacter, String spriteId, String spriteIdMirror) {
        this(compatibleCharacter, spriteId);
        this.spriteIdMirror = spriteIdMirror;
        this.mirror = true;
    }

    public void getFrames() {
        if (frames == null) {
            frames = new Animation<>(animationSpeed, ((TextureAtlas) HadalGame.assetManager.get(AssetList.COSMETICS_ATL.toString())).findRegions(spriteId));
            if (frames.getKeyFrames().length != 0) {
                cosmeticWidth = frames.getKeyFrame(0).getRegionWidth();
                cosmeticHeight = frames.getKeyFrame(0).getRegionHeight();
            }
        }
        if (mirror && framesMirror == null) {
            framesMirror = new Animation<>(animationSpeed, ((TextureAtlas) HadalGame.assetManager.get(AssetList.COSMETICS_ATL.toString())).findRegions(spriteIdMirror));
        }
    }

    public void render(SpriteBatch batch, float animationTimeExtra, float scale, boolean flip, float locationX, float locationY) {

        if (mirror && flip) {
            if (framesMirror == null) { getFrames(); }
            batch.draw(framesMirror.getKeyFrame(animationTimeExtra, true), locationX - cosmeticWidth * scale,
                    locationY,0, 0, cosmeticWidth * scale, cosmeticHeight * scale, 1, 1, 0);
        } else {
            if (frames == null) { getFrames(); }
            batch.draw(frames.getKeyFrame(animationTimeExtra, true), locationX, locationY,0, 0,
                    (flip ? -1 : 1) * cosmeticWidth * scale, cosmeticHeight * scale, 1, 1, 0);
        }
    }

    public Ragdoll createRagdoll(PlayState state, Vector2 playerLocation, float scale, Vector2 playerVelocity) {
        if (frames.getKeyFrames().length != 0) {
            return new Ragdoll(state, playerLocation, new Vector2(cosmeticWidth, cosmeticHeight).scl(scale),
                    frames.getKeyFrame(0), playerVelocity, gibDuration, gibGravity, true, false);
        }
        return null;
    }

    public UnlockCharacter getCompatibleCharacter() { return compatibleCharacter; }
}