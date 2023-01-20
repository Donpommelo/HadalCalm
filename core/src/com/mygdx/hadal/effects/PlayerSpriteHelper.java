package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
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

    private final Player player;

    //scale is changed for player size modifiers
    private float scale;

    private TextureRegion bodyBackSprite, armSprite, gemSprite, gemInactiveSprite;
    private Animation<TextureRegion> bodyStillSprite, bodyRunSprite, headSprite;
    private FrameBuffer fbo;

    private int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
        gemWidth, gemHeight;
    private final int toolWidth, toolHeight;

    private UnlockCharacter character;
    private AlignmentFilter team;

    public PlayerSpriteHelper(Player player, float scale) {
        this.player = player;
        this.scale = scale;

        this.toolWidth = player.getToolSprite().getRegionWidth();
        this.toolHeight = player.getToolSprite().getRegionHeight();
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

        //dispose of old frame buffer
        if (null != fbo) {
            fbo.dispose();
        }

        if (null != newCharacter) {
            this.character = newCharacter;
        }
        if (null != newTeam) {
            this.team = newTeam;
        }

        //obtain new texture and create new frame buffer object
        Texture tex = character.getTexture();
        TextureAtlas atlas = character.getAtlas();

        fbo = new FrameBuffer(Pixmap.Format.RGBA4444, tex.getWidth(), tex.getHeight(), true);
        fbo.begin();

        //clear buffer, set camera
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

        //use shader to apply new team color
        batch.begin();
        ShaderProgram shader;
        if (team.isTeam() && AlignmentFilter.NONE != team) {
            shader = team.getShader(character);
        } else {
            shader = character.getPalette().getShader(character);
        }
        batch.setShader(shader);

        batch.draw(tex, 0, 0);

        if (null != shader) {
            batch.setShader(null);
        }
        batch.end();
        fbo.end();

        if (null != shader) {
            shader.dispose();
        }

        //use new frame buffer to create texture regions for each body part.
        TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture());

        bodyRunSprite = copyFrames(fboRegion, atlas, "body_base_run", PlayState.SPRITE_ANIMATION_SPEED);
        bodyStillSprite = copyFrames(fboRegion, atlas, "body_base_stand", PlayState.SPRITE_ANIMATION_SPEED);
        headSprite = copyFrames(fboRegion, atlas, "head_base", PlayState.SPRITE_ANIMATION_SPEED);
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

        //head type cosmetics replace the head, so we don't want to draw the base head unless not rendering cosmetics,
        // or only rendering 1 non-head cosmetic
        boolean head = player.getPlayerData().getLoadout().cosmetics[CosmeticSlot.HEAD.getSlotNumber()].isBlank();
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
        if (null == lockedCosmetic) {
            //draw cosmetics. Use head coordinates. Update coordinates if any cosmetics replace the head
            for (UnlockCosmetic cosmetic : player.getPlayerData().getLoadout().cosmetics) {
                headLocation.set(cosmetic.render(batch, player.getPlayerData().getLoadout().team,
                        player.getPlayerData().getLoadout().character, animationTimeExtra, scale, flip, headLocation, bodyLocation));
            }
        } else {
            //only draw the locked cosmetic
            headLocation.set(lockedCosmetic.render(batch, player.getPlayerData().getLoadout().team,
                    player.getPlayerData().getLoadout().character, animationTimeExtra, scale, flip, headLocation, bodyLocation));
        }
    }

    /**
     * This is run when the player is disposed.
     * In the case of the player being disposed upon level transition, we want to make sure the fbo is cleaned up
     */
    public void dispose(DespawnType despawn) {
        if (DespawnType.LEVEL_TRANSITION.equals(despawn)) {
            if (null != fbo) {
                fbo.dispose();
            }
        }
    }

    /**
     * This is run when the player despawns from disconnecting or dying.
     */
    public void despawn(DespawnType type, Vector2 playerLocation, Vector2 playerVelocity) {
        switch (type) {
            case GIB:
                createGibs(playerLocation, playerVelocity);
                break;
            case VAPORIZE:
                createVaporization(playerLocation, playerVelocity);
                break;
            case TELEPORT:

                //if the player disconnects/becomes a spectator, we dispose of the fbo right away.
                if (null != fbo) {
                    fbo.dispose();
                }
                break;
        }
    }

    public static final float GIB_DURATION = 3.0f;
    public static final float GIB_GRAVITY = 1.0f;
    private void createGibs(Vector2 playerLocation, Vector2 playerVelocity) {

        //head type cosmetics replace the head, so we don't want to create a ragdoll for it
        if (player.getPlayerData().getLoadout().cosmetics[CosmeticSlot.HEAD.getSlotNumber()].isBlank()) {
            Ragdoll headRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(headWidth, headHeight).scl(scale),
                    headSprite.getKeyFrame(0), playerVelocity, GIB_DURATION, GIB_GRAVITY, true, false, true);

            if (!player.getState().isServer()) {
                ((ClientState) player.getState()).addEntity(headRagdoll.getEntityID(), headRagdoll, false, ClientState.ObjectLayer.STANDARD);
            }
        }

        Ragdoll bodyRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(bodyWidth, bodyHeight).scl(scale),
                bodyStillSprite.getKeyFrame(0), playerVelocity, GIB_DURATION, GIB_GRAVITY, true, false, true) {

            //we need to dispose of the fbo when the ragdolls are done
            @Override
            public void dispose() {
                super.dispose();
                if (null != fbo) {
                    fbo.dispose();
                }
            }
        };

        Ragdoll armRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(armWidth, armHeight).scl(scale),
                armSprite, playerVelocity, GIB_DURATION, GIB_GRAVITY, true, false, true);

        Ragdoll toolRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(toolWidth, toolHeight).scl(scale),
                player.getToolSprite(), playerVelocity, GIB_DURATION, GIB_GRAVITY, true, false, true);

        //Get cosmetic ragdolls
        for (UnlockCosmetic cosmetic : player.getPlayerData().getLoadout().cosmetics) {
            Ragdoll cosmeticRagdoll = cosmetic.createRagdoll(player.getState(), player.getPlayerData().getLoadout().team,
                    player.getPlayerData().getLoadout().character, playerLocation, scale, playerVelocity);
            if (null != cosmeticRagdoll && !player.getState().isServer()) {
                ((ClientState) player.getState()).addEntity(cosmeticRagdoll.getEntityID(), cosmeticRagdoll, false, ClientState.ObjectLayer.STANDARD);
            }
        }

        //the client needs to create ragdolls separately b/c we can't serialize the frame buffer object.
        if (!player.getState().isServer()) {
            ((ClientState) player.getState()).addEntity(bodyRagdoll.getEntityID(), bodyRagdoll, false, ClientState.ObjectLayer.STANDARD);
            ((ClientState) player.getState()).addEntity(armRagdoll.getEntityID(), armRagdoll, false, ClientState.ObjectLayer.STANDARD);
            ((ClientState) player.getState()).addEntity(toolRagdoll.getEntityID(), toolRagdoll, false, ClientState.ObjectLayer.STANDARD);
        }
    }

    private static final int RAGDOLL_WIDTH = 100;
    private static final int RAGDOLL_HEIGHT = 120;
    private void createVaporization(Vector2 playerLocation, Vector2 playerVelocity) {
        FrameBuffer ragdollBuffer = new FrameBuffer(Pixmap.Format.RGBA4444, RAGDOLL_WIDTH, RAGDOLL_HEIGHT, true);
        ragdollBuffer.begin();

        //clear buffer, set camera
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        player.getState().getBatch().getProjectionMatrix().setToOrtho2D(0, 0, ragdollBuffer.getWidth(), ragdollBuffer.getHeight());

        //render player
        player.getState().getBatch().begin();
        render(player.getState().getBatch(), player.getAttackAngle(), player.getMoveState(), 0.0f, 0.0f,
                false, new Vector2(RAGDOLL_WIDTH, RAGDOLL_HEIGHT).scl(0.5f), true, null, false);
        player.getState().getBatch().end();

        ragdollBuffer.end();
        TextureRegion ragdollTexture = new TextureRegion(ragdollBuffer.getColorBufferTexture(), 0,
                ragdollBuffer.getHeight(), ragdollBuffer.getWidth(), -ragdollBuffer.getHeight());

        Ragdoll bodyRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(RAGDOLL_WIDTH, RAGDOLL_HEIGHT),
                ragdollTexture, playerVelocity, 2.0f, 0.0f, true, false, false) {

            private Shader shader;
            private float progress;
            private float timer;
            private static final float FADE_DELAY = 0.25f;
            private static final float FADE_DURATION = 2.25f;
            @Override
            public void create() {
                super.create();

                //initiate shader used for vaporization effect
                shader = Shader.PERLIN_COLOR_FADE;
                shader.loadShader();
                body.setAngularDamping(4.0f);
                body.setLinearDamping(3.0f);
            }

            @Override
            public void controller(float delta) {
                super.controller(delta);
                manageTimer(delta);
            }

            @Override
            public void clientController(float delta) {
                super.clientController(delta);
                manageTimer(delta);
            }

            @Override
            public void render(SpriteBatch batch) {
                batch.setShader(shader.getShaderProgram());
                shader.shaderDefaultUpdate(progress);
                super.render(batch);
                batch.setShader(null);
            }

            //we need to dispose of the fbo when the ragdolls are done
            @Override
            public void dispose() {
                super.dispose();
                if (null != fbo) {
                    fbo.dispose();
                }
                ragdollBuffer.dispose();
            }

            /**
             * This keeps track of shader progress for fading
             */
            private void manageTimer(float delta) {
                timer += delta;
                if (timer >= FADE_DELAY) {
                    progress = Math.min(Math.max(0.0f, (timer - FADE_DELAY) / FADE_DURATION), 1.0f);
                }
            }
        };

        if (!player.getState().isServer()) {
            ((ClientState) player.getState()).addEntity(bodyRagdoll.getEntityID(), bodyRagdoll, false, ClientState.ObjectLayer.STANDARD);
        }
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
        VAPORIZE,
        TELEPORT,
    }
}
