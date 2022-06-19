package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.effects.PlayerSpriteHelper.gibDuration;
import static com.mygdx.hadal.effects.PlayerSpriteHelper.gibGravity;

/**
 * A CharacterCosmetic represents a single Character-Cosmetic relationship
 */
public class CharacterCosmetic {

    public static final float cosmeticAnimationSpeed = 0.05f;

    //The id of the sprite in thte cosmetic texture atlas
    private final String spriteId;

    //the character that this cosmetic is worn by
    private final UnlockCharacter compatibleCharacter;

    //frames of sprites for rendering this cosmetic
    private Animation<TextureRegion> frames, framesMirror;
    private final ObjectMap<String, ShadedSprite> shadedCosmetics = new ObjectMap<>();

    //dimensions of the cosmetic
    private float cosmeticWidth, cosmeticHeight, offsetX, offsetY;

    //modifies the offsets of remaining cosmetics (used for things like head replacements)
    private float offsetXRest, offsetYRest;

    //mirror indicates that a cosmetic is drawn with a different sprite when mirrored instead of just being flipped
    //this is used to properly render things like text
    private boolean mirror;
    private String spriteIdMirror;

    private boolean useShader;

    private Animation.PlayMode mode = Animation.PlayMode.LOOP;

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
    public Vector2 render(Batch batch, AlignmentFilter team, UnlockCharacter character, float animationTimeExtra,
                          float scale, boolean flip, Vector2 location) {

        if (useShader || team.isCosmeticApply()) {
            if (frames == null) {
                getFrames();
            }
            batch.draw(drawShadedCosmetic(batch, team, character, frames.getKeyFrames()).getKeyFrame(animationTimeExtra, true),
                    location.x + (flip ? -1 : 1) * offsetX * scale,
                    location.y + offsetY * scale, 0, 0, (flip ? -1 : 1) * cosmeticWidth * scale,
                    cosmeticHeight * scale, 1, 1, 0);
        } else {
            //mirrored sprites are drawn differently when character is flipped
            if (mirror && flip) {
                if (framesMirror == null) {
                    getFrames();
                }
                batch.draw(framesMirror.getKeyFrame(animationTimeExtra, true), location.x - cosmeticWidth * scale + offsetX * scale,
                        location.y + offsetY * scale, 0, 0, cosmeticWidth * scale,
                        cosmeticHeight * scale, 1, 1, 0);
            } else {
                if (frames == null) {
                    getFrames();
                }
                batch.draw(frames.getKeyFrame(animationTimeExtra, true), location.x + (flip ? -1 : 1) * offsetX * scale,
                        location.y + offsetY * scale, 0, 0, (flip ? -1 : 1) * cosmeticWidth * scale,
                        cosmeticHeight * scale, 1, 1, 0);
            }
        }


        return location.add(offsetXRest * scale, offsetYRest * scale);
    }

    /**
     * Called when player ragdolls are created to spawn a ragdoll for the cosmetic if applicable
     */
    public Ragdoll createRagdoll(PlayState state, AlignmentFilter team, UnlockCharacter character,
                                 Vector2 playerLocation, float scale, Vector2 playerVelocity) {
        if (frames == null) {
            getFrames();
        }
        if (frames.getKeyFrames().length != 0) {
            if (useShader || team.isCosmeticApply()) {
                return new Ragdoll(state, playerLocation, new Vector2(cosmeticWidth, cosmeticHeight).scl(scale),
                        drawShadedCosmetic(state.getBatch(), team, character, frames.getKeyFrames()).getKeyFrame(0),
                        playerVelocity, gibDuration, gibGravity, true, false, true);
            } else {
                return new Ragdoll(state, playerLocation, new Vector2(cosmeticWidth, cosmeticHeight).scl(scale),
                        frames.getKeyFrame(0), playerVelocity, gibDuration, gibGravity, true, false, true);
            }
        }
        return null;
    }

    private Animation<TextureRegion> drawShadedCosmetic(Batch batch, AlignmentFilter team, UnlockCharacter character, TextureRegion[] sprite) {
        String shaderKey;
        if (team.isTeam() && team != AlignmentFilter.NONE) {
            shaderKey = team.getTeamName();
        } else {
            shaderKey = character.getName();
        }
        ShadedSprite shadedSprite = shadedCosmetics.get(shaderKey);
        if (shadedSprite == null) {
            batch.end();
            shadedSprite = new ShadedSprite(batch, team, character, sprite, mode);
            batch.begin();

            shadedCosmetics.put(shaderKey, shadedSprite);
        }

        return shadedSprite.getAnimation();
    }

    private final Array<String> keysToRemove = new Array<>();
    public void clearShadedCosmetics(PlayState state, UnlockCosmetic cosmetic) {
        for (ObjectMap.Entry<String, ShadedSprite> sprite : shadedCosmetics.entries()) {
            boolean used = false;

            if (state.isServer()) {
                for (User user: HadalGame.server.getUsers().values()) {
                    if (user.getPlayer() != null) {
                        if (user.getPlayer().getPlayerData() != null) {
                            if (checkClearLoadout(user.getPlayer().getPlayerData().getLoadout(), sprite.key, cosmetic)) {
                                used = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                for (User user: HadalGame.client.getUsers().values()) {
                    if (user.getPlayer() != null) {
                        if (user.getPlayer().getPlayerData() != null) {
                            if (checkClearLoadout(user.getPlayer().getPlayerData().getLoadout(), sprite.key, cosmetic)) {
                                used = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (!used) {
                sprite.value.dispose();
                keysToRemove.add(sprite.key);
            }
            for (String s : keysToRemove) {
                shadedCosmetics.remove(s);
            }
            keysToRemove.clear();
        }
    }

    private boolean checkClearLoadout(Loadout loadout, String key, UnlockCosmetic cosmetic) {
        if (loadout == null) { return false; }
        if (loadout.team.getTeamName().equals(key) || loadout.character.getName().equals(key)) {
            return loadout.cosmetics[cosmetic.getCosmeticSlot().getSlotNumber()].equals(cosmetic);
        }
        return false;
    }

    public CharacterCosmetic setOffsetX(float offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public CharacterCosmetic setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public CharacterCosmetic setOffsetXRest(float offsetXRest) {
        this.offsetXRest = offsetXRest;
        return this;
    }

    public CharacterCosmetic setOffsetYRest(float offsetYRest) {
        this.offsetYRest = offsetYRest;
        return this;
    }

    public CharacterCosmetic setUseShader(boolean useShader) {
        this.useShader = useShader;
        return this;
    }

    public void setPlayMode(Animation.PlayMode mode) {
        this.mode = mode;
    }

    public UnlockCharacter getCompatibleCharacter() {
        return compatibleCharacter;
    }
}
