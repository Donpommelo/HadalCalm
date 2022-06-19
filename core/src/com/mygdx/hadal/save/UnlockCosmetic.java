package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.effects.CharacterCosmetic;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;

import java.util.HashMap;

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

    LONG_FACE(CosmeticSlot.HEAD, GameText.LONG_FACE, GameText.LONG_FACE_DESC, true,
            new CharacterCosmetic(UnlockCharacter.WANDA, "wanda_head_long").setUseShader(true).setOffsetYRest(144.6f)),
    ;

    private final GameText name, desc;
    private final Array<UnlockManager.UnlockTag> tags = new Array<>();

    //This is the slot that this cosmetic goes into
    private final CosmeticSlot cosmeticSlot;

    //does the cosmetic sprite ragdoll when the user is defeated?
    private final boolean ragdoll;

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

    /**
     * This is called when a player is rendered to render the cosmetic.
     * We find the character's version of the cosmetic and render it if existent
     */
    public Vector2 render(Batch batch, AlignmentFilter team, UnlockCharacter character, float animationTimeExtra,
                          float scale, boolean flip, Vector2 location) {
        if (!blank) {
            CharacterCosmetic cosmetic = cosmetics.get(character);
            if (cosmetic != null) {
                return cosmetic.render(batch, team, character, animationTimeExtra, scale, flip, location);
            }
        }
        return location;
    }

    /**
     * This is called when a player is ragdolled.
     * @return the cosmetic ragdoll
     */
    public Ragdoll createRagdoll(PlayState state, AlignmentFilter team, UnlockCharacter character,
                                 Vector2 playerLocation, float scale, Vector2 playerVelocity) {
        if (ragdoll) {
            CharacterCosmetic cosmetic = cosmetics.get(character);
            if (cosmetic != null) {
                return cosmetic.createRagdoll(state, team, character, playerLocation, scale, playerVelocity);
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

    public static void clearShadedCosmetics(PlayState state) {

        //we do a hub check here, since that is the only time a new color/cosmetic can be equipped (set team is run on respawn)
        if (state.getMode().isHub()) {
            for (UnlockCosmetic unlock : UnlockCosmetic.values()) {
                for (CharacterCosmetic cosmetic : unlock.cosmetics.values()) {
                    cosmetic.clearShadedCosmetics(state, unlock);
                }
            }
        }
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

