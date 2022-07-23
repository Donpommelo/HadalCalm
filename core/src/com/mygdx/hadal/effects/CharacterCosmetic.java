package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.effects.PlayerSpriteHelper.GIB_DURATION;
import static com.mygdx.hadal.effects.PlayerSpriteHelper.GIB_GRAVITY;

/**
 * A CharacterCosmetic represents a single Character-Cosmetic relationship
 */
public class CharacterCosmetic {

    public static final float COSMETIC_ANIMATION_SPEED = 0.05f;

    //The id of the sprite in thte cosmetic texture atlas
    private final String spriteId;

    //the character that this cosmetic is worn by
    private final UnlockCharacter compatibleCharacter;

    //frames of sprites for rendering this cosmetic
    private Animation<TextureRegion> frames, framesMirror;
    private final ObjectMap<String, ShadedSprite> shadedCosmetics = new ObjectMap<>();

    //dimensions of the cosmetic
    private float cosmeticWidth, cosmeticHeight, offsetX, offsetY;

    //modifies the offsets of yet-to-be rendered cosmetics (used for things like head replacements)
    private float offsetXRest, offsetYRest;

    //mirror indicates that a cosmetic is drawn with a different sprite when mirrored instead of just being flipped
    //this is used to properly render things like text
    private boolean mirror;
    private String spriteIdMirror;

    //should shaders be applied to this cosmetic?
    private boolean useShader;

    private Animation.PlayMode mode = Animation.PlayMode.LOOP;

    //weight used to determine chance of bot equipping this cosmetic
    private int botRandomWeight = 10;

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
    public Animation<TextureRegion> getFrames() {
        if (frames == null) {
            frames = new Animation<>(COSMETIC_ANIMATION_SPEED, ((TextureAtlas) HadalGame.assetManager.get(AssetList.COSMETICS_ATL.toString())).findRegions(spriteId));
            frames.setPlayMode(mode);
            if (frames.getKeyFrames().length != 0) {
                cosmeticWidth = frames.getKeyFrame(0).getRegionWidth();
                cosmeticHeight = frames.getKeyFrame(0).getRegionHeight();
            }
        }
        if (mirror && framesMirror == null) {
            framesMirror = new Animation<>(COSMETIC_ANIMATION_SPEED, ((TextureAtlas) HadalGame.assetManager.get(AssetList.COSMETICS_ATL.toString())).findRegions(spriteIdMirror));
        }
        return frames;
    }

    /**
     * Draw the cosmetic at the correct location
     */
    public Vector2 render(Batch batch, AlignmentFilter team, UnlockCharacter character, float animationTimeExtra,
                          float scale, boolean flip, Vector2 location) {

        //if using shader, get sprite from ShaderSprite fbo instead of frames
        if (useShader || team.isCosmeticApply()) {
            if (frames == null) {
                getFrames();
            }
            if (mirror) {
                if (framesMirror == null) {
                    getFrames();
                }
                batch.draw(drawShadedCosmetic(batch, team, character, true, flip).getKeyFrame(animationTimeExtra, true),
                        location.x - (flip ? 1 : 0) * cosmeticWidth * scale + offsetX * scale,
                        location.y + offsetY * scale, 0, 0, cosmeticWidth * scale,
                        cosmeticHeight * scale, 1, 1, 0);
            } else {
                batch.draw(drawShadedCosmetic(batch, team, character, false, false).getKeyFrame(animationTimeExtra, true),
                        location.x + (flip ? -1 : 1) * offsetX * scale,
                        location.y + offsetY * scale, 0, 0, (flip ? -1 : 1) * cosmeticWidth * scale,
                        cosmeticHeight * scale, 1, 1, 0);
            }
        } else {
            //mirrored sprites are drawn differently when character is flipped
            if (mirror && flip) {
                if (framesMirror == null) {
                    getFrames();
                }
                batch.draw(framesMirror.getKeyFrame(animationTimeExtra, true),
                        location.x - cosmeticWidth * scale + offsetX * scale,
                        location.y + offsetY * scale, 0, 0, cosmeticWidth * scale,
                        cosmeticHeight * scale, 1, 1, 0);
            } else {
                if (frames == null) {
                    getFrames();
                }
                batch.draw(frames.getKeyFrame(animationTimeExtra, true),
                        location.x + (flip ? -1 : 1) * offsetX * scale,
                        location.y + offsetY * scale, 0, 0, (flip ? -1 : 1) * cosmeticWidth * scale,
                        cosmeticHeight * scale, 1, 1, 0);
            }
        }

        //return offsets with modification (if using cosmetics that change offsets for other cosmetics)
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
                        drawShadedCosmetic(state.getBatch(), team, character, false, false).getKeyFrame(0),
                        playerVelocity, GIB_DURATION, GIB_GRAVITY, true, false, true);
            } else {
                return new Ragdoll(state, playerLocation, new Vector2(cosmeticWidth, cosmeticHeight).scl(scale),
                        frames.getKeyFrame(0), playerVelocity, GIB_DURATION, GIB_GRAVITY, true, false, true);
            }
        }
        return null;
    }

    /**
     * This draws cosmetics with shaders applied.
     * Shaded sprites are cached into a hash map
     */
    private Animation<TextureRegion> drawShadedCosmetic(Batch batch, AlignmentFilter team, UnlockCharacter character,
                                                        boolean mirror, boolean flip) {
        //hashmap key is the team name (or character name if no team)
        String shaderKey;
        if (team.isTeam() && team != AlignmentFilter.NONE) {
            shaderKey = team.getTeamName();
        } else {
            shaderKey = character.getName();
        }

        ShadedSprite shadedSprite = shadedCosmetics.get(shaderKey);

        //if we don't have this shader-cosmetic combination cached, we create a new shaded sprite
        if (shadedSprite == null) {

            //because this is usually run during the render cycle, we need to set turn the batch on/off
            boolean drawing = batch.isDrawing();
            if (drawing) {
                batch.end();
            }

            if (mirror) {
                shadedSprite = new ShadedSprite(batch, team, character, frames.getKeyFrames(), framesMirror.getKeyFrames(), mode);
            } else {
                shadedSprite = new ShadedSprite(batch, team, character, frames.getKeyFrames(), mode);
            }

            if (drawing) {
                batch.begin();
            }

            //shaderKey can be null for clients if the player hasn't been initialized yet
            if (shaderKey != null) {
                shadedCosmetics.put(shaderKey, shadedSprite);
            }
        }

        return flip ? shadedSprite.getAnimationMirror() : shadedSprite.getAnimation();
    }

    /**
     * This is run whenever a player sets their color or cosmetic in the hub.
     * It clears all cached fbos that are not currently being used by a player
     */
    public void clearShadedCosmetics() {
        for (ObjectMap.Entry<String, ShadedSprite> sprite : shadedCosmetics.entries()) {
            if (sprite.value != null) {
                sprite.value.dispose();
            }
            shadedCosmetics.clear();
        }
    }

    public Animation<TextureRegion> getShadedFrames(Batch batch, AlignmentFilter team, UnlockCharacter character) {
        if (useShader || team.isCosmeticApply()) {
            getFrames();
            return drawShadedCosmetic(batch, team, character, mirror, false);
        } else {
            return getFrames();
        }
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

    public CharacterCosmetic setBotRandomWeight(int botRandomWeight) {
        this.botRandomWeight = botRandomWeight;
        return this;
    }

    public int getBotRandomWeight() { return botRandomWeight; }

    public void setPlayMode(Animation.PlayMode mode) {
        this.mode = mode;
    }

    public UnlockCharacter getCompatibleCharacter() {
        return compatibleCharacter;
    }
}
