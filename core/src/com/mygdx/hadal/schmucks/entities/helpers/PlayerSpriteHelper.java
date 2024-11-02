package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.SpriteConstants;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.RagdollManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.PlayerMiscUtil;

/**
 * The PlayerSpriteHelper helps draw the player sprite. Each player has a PlayerSpriteHelper.
 * This creates a frame buffer object whenever the player spawns or changes costume (character or color)
 * This frame buffer is used to draw palette swaps for the player body and frags.
 * @author Clecton Cloppet
 */
public class PlayerSpriteHelper {

    //Dimension of player sprite parts.
    public static final int HB_WIDTH = 216;
    public static final int HB_HEIGHT = 516;

    private static final int BODY_CONNECT_X = -100;
    private static final int BODY_CONNECT_Y = 0;

    private static final int HEAD_CONNECT_X = -26;
    private static final int HEAD_CONNECT_Y = 330;

    private static final int ARM_CONNECT_X = -304;
    private static final int ARM_CONNECT_Y = 218;

    private static final int ARM_ROTATE_X = 330;
    private static final int ARM_ROTATE_Y = 50;

    private final PlayState state;
    private final Player player;

    //scale is changed for player size modifiers
    private float scale;

    private TextureRegion bodyBackSprite, armSprite, gemSprite, gemInactiveSprite;
    private Animation<TextureRegion> bodyStillSprite, bodyRunSprite, headSprite;

    private int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
        gemWidth, gemHeight, toolWidth, toolHeight;

    private UnlockCharacter character;
    private AlignmentFilter team;

    public PlayerSpriteHelper(PlayState state, Player player, float scale) {
        this.state = state;
        this.player = player;
        this.scale = scale;

        //tool sprite is null in the case of headless server
        if (player.getToolSprite() != null) {
            this.toolWidth = player.getToolSprite().getRegionWidth();
            this.toolHeight = player.getToolSprite().getRegionHeight();
        }
    }

    /**
     * This is called when the player spawns or changes costumes
     * @param batch: the sprite batch to use to draw
     * @param character: the new character to draw
     * @param team: the new team color to draw
     */
    public void setBodySprite(SpriteBatch batch, UnlockCharacter character, AlignmentFilter team) {
        boolean replace = this.character != character && null != character;

        //replace frame buffer if the input contains a new character or team
        if (this.team != team && null != team) {
            replace = true;
        }

        if (replace) {
            replaceBodySprite(batch, character, team);
        }
    }

    /**
     * This actually draws the player's new frame buffer
     * @param batch: the sprite batch to draw the new sprite to
     * @param newCharacter: the new character to draw
     * @param newTeam: the new team color to draw
     */
    public void replaceBodySprite(SpriteBatch batch, UnlockCharacter newCharacter, AlignmentFilter newTeam) {

        //return if headless server
        if (HadalGame.assetManager == null) { return; }

        if (null != newCharacter) {
            this.character = newCharacter;
        }
        if (null != newTeam) {
            this.team = newTeam;
        }

        //obtain new texture and create new frame buffer object
        TextureAtlas atlas = character.getAtlas();

        FrameBuffer fbo = FrameBufferManager.getFrameBuffer(batch, character, team);

        //use new frame buffer to create texture regions for each body part.
        TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture());

        bodyRunSprite = copyFrames(fboRegion, atlas, "body_base_run", SpriteConstants.SPRITE_ANIMATION_SPEED);
        bodyStillSprite = copyFrames(fboRegion, atlas, "body_base_stand", SpriteConstants.SPRITE_ANIMATION_SPEED);
        headSprite = copyFrames(fboRegion, atlas, "head_base", SpriteConstants.SPRITE_ANIMATION_SPEED);
        bodyBackSprite = copyFrame(fboRegion, atlas, "body_bg_base");
        armSprite = copyFrame(fboRegion, atlas, "arm_base");
        gemSprite = atlas.findRegion("gem_active");
        gemInactiveSprite = atlas.findRegion("gem_inactive");

        //obtain body dimensions
        this.armWidth = armSprite.getRegionWidth();
        this.armHeight = armSprite.getRegionHeight();
        this.headWidth = headSprite.getKeyFrame(0).getRegionWidth();
        this.headHeight = headSprite.getKeyFrame(0).getRegionHeight();
        this.bodyWidth = bodyRunSprite.getKeyFrame(0).getRegionWidth();
        this.bodyHeight = bodyRunSprite.getKeyFrame(0).getRegionHeight();
        this.bodyBackWidth = bodyBackSprite.getRegionWidth();
        this.bodyBackHeight = bodyBackSprite.getRegionHeight();
        this.gemHeight = gemSprite.getRegionHeight();
        this.gemWidth = gemSprite.getRegionWidth();
    }

    private final Vector2 headLocation = new Vector2();
    private final Vector2 bodyLocation = new Vector2();
    /**
     * @param batch: sprite batch to render player to
     * @param attackAngle: the angle of the player's arm
     * @param moveState: is the player currently moving?
     * @param animationTime: the animation frame of the current sprite
     * @param animationTimeExtra: the animation frame of the current sprite's head
     * @param grounded: is the player on the ground?
     * @param playerLocation: where is the player located at?
     * @param cosmetics: Do we render the player's cosmetics?
     * @param lockedCosmetic: If not null, this is the 1 cosmetic we should render (used for haberdasher preview)
     * @param bob: Should the sprite bob up and down (false for kill feed sprite busts)
     */
    public void render(Batch batch, float attackAngle, MoveState moveState, float animationTime, float animationTimeExtra,
                       boolean grounded, Vector2 playerLocation, boolean cosmetics, UnlockCosmetic lockedCosmetic, boolean bob) {

        //flip determines if the player is facing left or right
        boolean flip = Math.abs(attackAngle) > 90;

        //Depending on which way the player is facing, the connection points of various body parts are slightly offset.
        float armConnectXReal = ARM_CONNECT_X;
        float headConnectXReal = HEAD_CONNECT_X;
        float armRotateXReal = ARM_ROTATE_X;

        float realAttackAngle = attackAngle;
        if (flip) {
            armConnectXReal = bodyWidth - armWidth - ARM_CONNECT_X - 200;
            headConnectXReal = bodyWidth - headWidth - HEAD_CONNECT_X - 200;
            armRotateXReal = armWidth - ARM_ROTATE_X;
            realAttackAngle += 180;
        }

        //offset head is separate for some characters to have head bobbing
        float yOffset;
        float yOffsetHead;
        boolean moving = MoveState.MOVE_LEFT.equals(moveState) || MoveState.MOVE_RIGHT.equals(moveState);
        int bodyFrame = bodyRunSprite.getKeyFrameIndex(animationTime);
        int headFrame = bodyRunSprite.getKeyFrameIndex(animationTimeExtra);

        yOffset = character.getWobbleOffsetBody(bob ? bodyFrame : 0, grounded, moving);
        yOffsetHead = character.getWobbleOffsetHead(bob ? bodyFrame : 0, bob ? headFrame : 0, grounded, moving);

        float bodyX = (flip ? bodyWidth * scale : 0) + playerLocation.x - HB_WIDTH * scale / 2  + BODY_CONNECT_X * scale;
        float bodyY = playerLocation.y - HB_HEIGHT * scale / 2  + BODY_CONNECT_Y + yOffset * scale;

        //Draw a bunch of stuff
        batch.draw(player.getToolSprite(),
            (flip ? toolWidth * scale : 0) + playerLocation.x - HB_WIDTH * scale / 2 + armConnectXReal * scale,
            playerLocation.y - HB_HEIGHT * scale / 2 + ARM_CONNECT_Y * scale + yOffset * scale,
            (flip ? -armWidth * scale : 0) + armRotateXReal * scale , ARM_ROTATE_Y * scale,
            (flip ? -1 : 1) * toolWidth * scale, toolHeight * scale, 1, 1, realAttackAngle);

        batch.draw(bodyBackSprite,
            (flip ? bodyBackWidth * scale : 0) + playerLocation.x - HB_WIDTH * scale / 2 + BODY_CONNECT_X * scale,
            playerLocation.y - HB_HEIGHT * scale / 2 + BODY_CONNECT_Y + yOffset * scale, 0, 0,
            (flip ? -1 : 1) * bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);

        batch.draw(armSprite,
            (flip ? armWidth * scale : 0) + playerLocation.x - HB_WIDTH * scale / 2 + armConnectXReal * scale,
            playerLocation.y - HB_HEIGHT * scale / 2 + ARM_CONNECT_Y * scale + yOffset * scale,
            (flip ? -armWidth * scale : 0) + armRotateXReal * scale, ARM_ROTATE_Y * scale,
            (flip ? -1 : 1) * armWidth * scale, armHeight * scale, 1, 1, realAttackAngle);

        batch.draw(gemSprite,
            (flip ? gemWidth * scale : 0) + playerLocation.x - HB_WIDTH * scale / 2  + BODY_CONNECT_X * scale,
            playerLocation.y - HB_HEIGHT * scale / 2 + BODY_CONNECT_Y + yOffset * scale, 0, 0,
            (flip ? -1 : 1) * gemWidth * scale, gemHeight * scale, 1, 1, 0);

        //reverse determines whether the player is running forwards or backwards.
        if (MoveState.MOVE_LEFT.equals(moveState)) {

            if (Math.abs(realAttackAngle) > 90) {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
            } else {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP);
            }
            batch.draw(bodyRunSprite.getKeyFrame(animationTime), bodyX, bodyY, 0, 0,
                (flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
        } else if (MoveState.MOVE_RIGHT.equals(moveState)) {
            if (Math.abs(realAttackAngle) < 90) {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
            } else {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP);
            }
            batch.draw(bodyRunSprite.getKeyFrame(animationTime), bodyX, bodyY, 0, 0,
                (flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
        } else {
            bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP);
            batch.draw(grounded ? bodyStillSprite.getKeyFrame(animationTime, true) :
                    bodyRunSprite.getKeyFrame(PlayerMiscUtil.getFreezeFrame(player.getLinearVelocity(), false)), bodyX, bodyY, 0, 0,
                (flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
        }

        float headX = (flip ? headWidth * scale : 0) + playerLocation.x - HB_WIDTH * scale / 2 + headConnectXReal * scale;
        float headY = playerLocation.y - HB_HEIGHT * scale / 2 + HEAD_CONNECT_Y * scale + yOffsetHead * scale;

        //head type cosmetics replace the head, so we don't want to draw the base head unless not rendering cosmetics or only rendering 1 non-head cosmetic
        boolean head = player.getUser().getLoadoutManager().getActiveLoadout().cosmetics[CosmeticSlot.HEAD.getSlotNumber()].isBlank();
        if (null != lockedCosmetic) {
            head = true;
            if (CosmeticSlot.HEAD == lockedCosmetic.getCosmeticSlot()) {
                head = lockedCosmetic.isBlank();
            }
        }
        if (head || !cosmetics) {
            batch.draw(headSprite.getKeyFrame(animationTimeExtra, true), headX, headY,0, 0,
                    (flip ? -1 : 1) * headWidth * scale, headHeight * scale, 1, 1, 0);
        }
        headLocation.set(headX, headY);
        bodyLocation.set(bodyX, bodyY);

        if (cosmetics) {
            renderCosmetics(batch, animationTimeExtra, flip, lockedCosmetic);
        }
    }

    /**
     * Helper method that renders the player's cosmetics
     */
    private void renderCosmetics(Batch batch, float animationTimeExtra, boolean flip, UnlockCosmetic lockedCosmetic) {
        Loadout loadout = player.getUser().getLoadoutManager().getActiveLoadout();
        if (null == lockedCosmetic) {
            //draw cosmetics. Use head coordinates. Update coordinates if any cosmetics replace the head
            for (UnlockCosmetic cosmetic : loadout.cosmetics) {
                headLocation.set(cosmetic.render(batch, loadout.team,
                        loadout.character, animationTimeExtra, scale, flip, headLocation, bodyLocation));
            }
        } else {
            //only draw the locked cosmetic
            headLocation.set(lockedCosmetic.render(batch, loadout.team, loadout.character, animationTimeExtra, scale,
                    flip, headLocation, bodyLocation));
        }
    }

    /**
     * This is run when the player despawns from disconnecting or dying.
     */
    public void despawn(DespawnType type, Vector2 playerLocation, Vector2 playerVelocity) {

        //return in case of headless server
        if (HadalGame.assetManager == null) { return; }

        switch (type) {
            case GIB -> createGibs(playerLocation, playerVelocity);
            case BIFURCATE -> createBifurcation(playerLocation, playerVelocity);
            case VAPORIZE -> createVaporization(playerLocation, playerVelocity);
            case TELEPORT -> createWarpAnimation(playerLocation);
        }
    }

    public static final float GIB_DURATION = 3.0f;
    public static final float GIB_GRAVITY = 1.0f;
    private void createGibs(Vector2 playerLocation, Vector2 playerVelocity) {
        Loadout loadout = player.getUser().getLoadoutManager().getActiveLoadout();

        RagdollCreate ragdollCreate = new RagdollCreate()
                .setPosition(playerLocation)
                .setVelocity(playerVelocity)
                .setLifespan(GIB_DURATION)
                .setGravity(GIB_GRAVITY)
                .setStartVelocity(true)
                .setFade();

        //head type cosmetics replace the head, so we don't want to create a ragdoll for it
        if (loadout.cosmetics[CosmeticSlot.HEAD.getSlotNumber()].isBlank()) {
            ragdollCreate.setTextureRegion(headSprite.getKeyFrame(0)).setSize(new Vector2(headWidth, headHeight).scl(scale));
            RagdollManager.getRagdoll(state, ragdollCreate);
        }

        ragdollCreate.setTextureRegion(bodyStillSprite.getKeyFrame(0)).setSize(new Vector2(bodyWidth, bodyHeight).scl(scale));
        RagdollManager.getRagdoll(state, ragdollCreate);

        ragdollCreate.setTextureRegion(armSprite).setSize(new Vector2(armWidth, armHeight).scl(scale));
        RagdollManager.getRagdoll(state, ragdollCreate);

        ragdollCreate.setTextureRegion(player.getToolSprite()).setSize(new Vector2(toolWidth, toolHeight).scl(scale));
        RagdollManager.getRagdoll(state, ragdollCreate);

        //Get cosmetic ragdolls
        for (UnlockCosmetic cosmetic : loadout.cosmetics) {
            cosmetic.createRagdoll(state, loadout.team, loadout.character, playerLocation, scale, playerVelocity);
        }
    }

    private static final int RAGDOLL_WIDTH = 100;
    private static final int RAGDOLL_HEIGHT = 120;
    private void createVaporization(Vector2 playerLocation, Vector2 playerVelocity) {
        FrameBuffer ragdollBuffer = getRagdollBuffer();
        TextureRegion ragdollTexture = new TextureRegion(ragdollBuffer.getColorBufferTexture(), 0,
                RAGDOLL_FBO_HEIGHT, RAGDOLL_FBO_WIDTH, -RAGDOLL_FBO_HEIGHT);

        RagdollManager.getRagdollFBO(state, new RagdollCreate()
                .setTextureRegion(ragdollTexture)
                .setPosition(playerLocation)
                .setSize(new Vector2(RAGDOLL_WIDTH, RAGDOLL_HEIGHT).scl(1.0f + player.getScaleModifier()))
                .setVelocity(playerVelocity)
                .setLifespan(2.0f)
                .setGravity(GIB_GRAVITY)
                .setDampening(4.0f, 3.0f)
                .setStartVelocity(true)
                .setFade(1.75f, Shader.PERLIN_COLOR_FADE), ragdollBuffer);
    }

    private void createBifurcation(Vector2 playerLocation, Vector2 playerVelocity) {
        FrameBuffer ragdollBuffer = getRagdollBuffer();
        TextureRegion ragdollTexture1 = new TextureRegion(ragdollBuffer.getColorBufferTexture(), 0,
                RAGDOLL_FBO_HEIGHT, RAGDOLL_FBO_WIDTH, -RAGDOLL_FBO_HEIGHT / 2);
        TextureRegion ragdollTexture2 = new TextureRegion(ragdollBuffer.getColorBufferTexture(), 0,
                RAGDOLL_FBO_HEIGHT / 2, RAGDOLL_FBO_WIDTH, -RAGDOLL_FBO_HEIGHT / 2);

        RagdollCreate ragdollCreate = new RagdollCreate()
                .setPosition(playerLocation)
                .setSize(new Vector2(RAGDOLL_WIDTH, RAGDOLL_HEIGHT / 2.0f).scl(1.0f + player.getScaleModifier()))
                .setVelocity(playerVelocity)
                .setLifespan(GIB_DURATION)
                .setGravity(GIB_GRAVITY)
                .setStartVelocity(true)
                .setFade();

        RagdollManager.getRagdollFBO(state, ragdollCreate.setTextureRegion(ragdollTexture1), ragdollBuffer);
        RagdollManager.getRagdoll(state, ragdollCreate.setTextureRegion(ragdollTexture2).setSpinning(false));
    }

    private void createWarpAnimation(Vector2 playerLocation) {
        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.TELEPORT,
                playerLocation.sub(0, player.getSize().y / 2))
                .setLifespan(1.0f));
    }

    private static final int RAGDOLL_FBO_WIDTH = 667;
    private static final int RAGDOLL_FBO_HEIGHT = 800;
    private FrameBuffer getRagdollBuffer() {
        FrameBuffer ragdollBuffer = new FrameBuffer(Pixmap.Format.RGBA4444, RAGDOLL_FBO_WIDTH, RAGDOLL_FBO_HEIGHT, false);
        ragdollBuffer.begin();

        //clear buffer, set camera
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        state.getBatch().getProjectionMatrix().setToOrtho2D(0, 0, ragdollBuffer.getWidth(), ragdollBuffer.getHeight());

        //render player
        state.getBatch().begin();

        //keep track of scale here to make fbo consistent size, but frags scale to size modifiers
        float scaleTemp = scale;
        setScale(1.0f);
        render(state.getBatch(), player.getMouseHelper().getAttackAngle(), player.getMoveState(),
                0.0f, 0.0f,false, new Vector2(RAGDOLL_FBO_WIDTH, RAGDOLL_FBO_HEIGHT).scl(0.5f),
                true, null, false);
        setScale(scaleTemp);
        state.getBatch().end();

        ragdollBuffer.end();

        return ragdollBuffer;
    }

    /**
     * These 2 methods copy animations and still images from the new frame buffer using the old texture atlas.
     */
    private Animation<TextureRegion> copyFrames(TextureRegion tex, TextureAtlas atlas, String region, float animationSpeed) {
        Array<TextureRegion> frames = new Array<>();
        for (TextureRegion texRegion : atlas.findRegions(region)) {
            frames.add(new TextureRegion(tex, texRegion.getRegionX(), tex.getRegionHeight() - texRegion.getRegionY(),
                texRegion.getRegionWidth(), -texRegion.getRegionHeight()));
        }
        return new Animation<>(animationSpeed, frames);
    }

    private TextureRegion copyFrame(TextureRegion tex, TextureAtlas atlas, String region) {
        TextureRegion texRegion = atlas.findRegion(region);
        return new TextureRegion(tex, texRegion.getRegionX(), tex.getRegionHeight() - texRegion.getRegionY(),
            texRegion.getRegionWidth(), -texRegion.getRegionHeight());
    }

    public void setScale(float scale) { this.scale = scale; }

    public enum DespawnType {
        LEVEL_TRANSITION,
        GIB,
        BIFURCATE,
        VAPORIZE,
        TELEPORT,
    }
}
