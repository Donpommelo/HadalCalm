package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class PlayerSpriteHelper {

    //Dimension of player sprite parts.
    public static final int hbWidth = 216;
    public static final int hbHeight = 516;

    private static final int bodyConnectX = -100;
    private static final int bodyConnectY = 0;

    private static final int headConnectX = -26;
    private static final int headConnectY = 330;

    private static final int armConnectX = -304;
    private static final int armConnectY = 218;

    private static final int armRotateX = 330;
    private static final int armRotateY = 50;

    private final Player player;
    private final float scale;

    private TextureRegion bodyBackSprite, armSprite, gemSprite;
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

    public void setBodySprite(SpriteBatch batch, UnlockCharacter character, AlignmentFilter team) {
        boolean replace = false;

        if (this.character != character && character != null) {
            replace = true;
        }

        if (this.team != team && team != null) {
            replace = true;
        }

        if (replace) {
            replaceBodySprite(batch, character, team);
        }
    }

    public void replaceBodySprite(SpriteBatch batch, UnlockCharacter newCharacter, AlignmentFilter newTeam) {
        if (fbo != null) {
            fbo.dispose();
        }

        if (newCharacter != null) {
            this.character = newCharacter;
        }
        if (newTeam != null) {
            this.team = newTeam;
        }

        Texture tex = character.getTexture();
        TextureAtlas atlas = character.getAtlas();
        fbo = new FrameBuffer(Pixmap.Format.RGBA4444, tex.getWidth(), tex.getHeight(), true);

        fbo.begin();

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

        batch.begin();
        ShaderProgram shader = null;
        if (team.isTeam() && team != AlignmentFilter.NONE) {
            shader = team.getShader(character);
            batch.setShader(shader);
        }

        batch.draw(tex, 0, 0);

        if (shader != null) {
            batch.setShader(null);
        }
        batch.end();
        fbo.end();

        if (shader != null) {
            shader.dispose();
        }

        TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture());

        bodyRunSprite =  copyFrames(fboRegion, atlas, "body_run", PlayState.spriteAnimationSpeed);
        bodyStillSprite =  copyFrames(fboRegion, atlas, "body_stand", PlayState.spriteAnimationSpeed);
        headSprite =  copyFrames(fboRegion, atlas, "head", PlayState.spriteAnimationSpeed);
        bodyBackSprite = copyFrame(fboRegion, atlas, "body_background");
        armSprite = copyFrame(fboRegion, atlas, "arm");
        gemSprite = copyFrame(fboRegion, atlas, "gem_active");

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

    public void render(SpriteBatch batch, float attackAngle, MoveState moveState, float animationTime, float animationTimeExtra,
                       boolean grounded, Vector2 playerLocation) {

        //flip determines if the player is facing left or right
        boolean flip = Math.abs(attackAngle) > 90;

        //Depending on which way the player is facing, the connection points of various body parts are slightly offset.
        float armConnectXReal = armConnectX;
        float headConnectXReal = headConnectX;
        float armRotateXReal = armRotateX;

        float realAttackAngle = attackAngle;
        if (flip) {
            armConnectXReal = bodyWidth - armWidth - armConnectX - 200;
            headConnectXReal = bodyWidth - headWidth - headConnectX - 200;
            armRotateXReal = armWidth - armRotateX;
            realAttackAngle += 180;
        }

        //This switch determines the total body y-offset to make the body bob up and down when running.
        //offset head is separate for some characters to have head bobbing
        float yOffset;
        float yOffsetHead;
        boolean moving = moveState.equals(MoveState.MOVE_LEFT) || moveState.equals(MoveState.MOVE_RIGHT);
        int bodyFrame = bodyRunSprite.getKeyFrameIndex(animationTime);
        int headFrame = bodyRunSprite.getKeyFrameIndex(animationTimeExtra);

        yOffset = character.getWobbleOffsetBody(bodyFrame, grounded, moving);
        yOffsetHead = character.getWobbleOffsetHead(bodyFrame, headFrame, grounded, moving);

        //Draw a bunch of stuff
        batch.draw(player.getToolSprite(),
            (flip ? toolWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2 + armConnectXReal * scale,
            playerLocation.y - hbHeight * scale / 2 + armConnectY * scale + yOffset,
            (flip ? -armWidth * scale : 0) + armRotateXReal * scale , armRotateY * scale,
            (flip ? -1 : 1) * toolWidth * scale, toolHeight * scale, 1, 1, realAttackAngle);

        batch.draw(bodyBackSprite,
            (flip ? bodyBackWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2 + bodyConnectX * scale,
            playerLocation.y - hbHeight * scale / 2 + bodyConnectY + yOffset,
            0, 0,
            (flip ? -1 : 1) * bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);

        batch.draw(armSprite,
            (flip ? armWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2 + armConnectXReal * scale,
            playerLocation.y - hbHeight * scale / 2 + armConnectY * scale + yOffset,
            (flip ? -armWidth * scale : 0) + armRotateXReal * scale, armRotateY * scale,
            (flip ? -1 : 1) * armWidth * scale, armHeight * scale, 1, 1, realAttackAngle);

        batch.draw(gemSprite,
            (flip ? gemWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2  + bodyConnectX * scale,
            playerLocation.y - hbHeight * scale / 2 + bodyConnectY + yOffset,
            0, 0,
            (flip ? -1 : 1) * gemWidth * scale, gemHeight * scale, 1, 1, 0);

        //reverse determines whether the player is running forwards or backwards.
        if (moveState.equals(MoveState.MOVE_LEFT)) {

            if (Math.abs(realAttackAngle) > 90) {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
            } else {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP);
            }

            batch.draw(bodyRunSprite.getKeyFrame(animationTime),
                (flip ? bodyWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2  + bodyConnectX * scale,
                playerLocation.y - hbHeight * scale / 2  + bodyConnectY + yOffset,
                0, 0,
                (flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
        } else if (moveState.equals(MoveState.MOVE_RIGHT)) {
            if (Math.abs(realAttackAngle) < 90) {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
            } else {
                bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP);
            }

            batch.draw(bodyRunSprite.getKeyFrame(animationTime),
                (flip ? bodyWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2  + bodyConnectX * scale,
                playerLocation.y - hbHeight * scale / 2  + bodyConnectY + yOffset,
                0, 0,
                (flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
        } else {
            bodyRunSprite.setPlayMode(Animation.PlayMode.LOOP);
            batch.draw(grounded ? bodyStillSprite.getKeyFrame(animationTime, true) :
                    bodyRunSprite.getKeyFrame(player.getFreezeFrame(false)),
                (flip ? bodyWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2  + bodyConnectX * scale,
                playerLocation.y - hbHeight * scale / 2  + bodyConnectY + yOffset,
                0, 0,
                (flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
        }

        batch.draw(headSprite.getKeyFrame(animationTimeExtra, true),
            (flip ? headWidth * scale : 0) + playerLocation.x - hbWidth * scale / 2 + headConnectXReal * scale,
            playerLocation.y - hbHeight * scale / 2 + headConnectY * scale + yOffsetHead,
            0, 0,
            (flip ? -1 : 1) * headWidth * scale, headHeight * scale, 1, 1, 0);
    }

    public void despawn(DespawnType type, Vector2 playerLocation, Vector2 playerVelocity) {
        switch (type) {
            case GIB:
                createGibs(playerLocation, playerVelocity);
                break;
            case TELEPORT:
                if (fbo != null) {
                    fbo.dispose();
                }
                break;
        }
    }

    private static final float gibDuration = 3.0f;
    private static final float gibGravity = 1.0f;
    public void createGibs(Vector2 playerLocation, Vector2 playerVelocity) {
        Ragdoll headRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(headWidth, headHeight).scl(scale),
					headSprite.getKeyFrame(0), playerVelocity, gibDuration, gibGravity, true, false) {

            @Override
            public void dispose() {
                super.dispose();
                if (fbo != null) {
                    fbo.dispose();
                }
            }
        };

        Ragdoll bodyRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(bodyWidth, bodyHeight).scl(scale),
                bodyStillSprite.getKeyFrame(0), playerVelocity, gibDuration, gibGravity, true, false);

        Ragdoll armRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(armWidth, armHeight).scl(scale),
                armSprite, playerVelocity, gibDuration, gibGravity, true, false);

        Ragdoll backRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(bodyBackWidth, bodyBackHeight).scl(scale),
                bodyBackSprite, playerVelocity, gibDuration, gibGravity, true, false);

        Ragdoll gemRagdoll =  new Ragdoll(player.getState(), playerLocation, new Vector2(gemWidth, gemHeight).scl(scale),
                gemSprite, playerVelocity, gibDuration, gibGravity, true, false);

        Ragdoll toolRagdoll = new Ragdoll(player.getState(), playerLocation, new Vector2(toolWidth, toolHeight).scl(scale),
                player.getToolSprite(), playerVelocity, gibDuration, gibGravity, true, false);

        if (!player.getState().isServer()) {
            ((ClientState) player.getState()).addEntity(headRagdoll.getEntityID().toString(), headRagdoll, false, ClientState.ObjectSyncLayers.STANDARD);
            ((ClientState) player.getState()).addEntity(bodyRagdoll.getEntityID().toString(), bodyRagdoll, false, ClientState.ObjectSyncLayers.STANDARD);
            ((ClientState) player.getState()).addEntity(armRagdoll.getEntityID().toString(), armRagdoll, false, ClientState.ObjectSyncLayers.STANDARD);
            ((ClientState) player.getState()).addEntity(backRagdoll.getEntityID().toString(), backRagdoll, false, ClientState.ObjectSyncLayers.STANDARD);
            ((ClientState) player.getState()).addEntity(gemRagdoll.getEntityID().toString(), gemRagdoll, false, ClientState.ObjectSyncLayers.STANDARD);
            ((ClientState) player.getState()).addEntity(toolRagdoll.getEntityID().toString(), toolRagdoll, false, ClientState.ObjectSyncLayers.STANDARD);
        }
    }

    private Animation<TextureRegion> copyFrames(TextureRegion tex, TextureAtlas atlas, String region, float animationSpeed) {
        Array<TextureRegion> frames = new Array<>();
        for (TextureRegion texRegion: atlas.findRegions(region)) {
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

    public enum DespawnType {
        GIB,
        TELEPORT,
    }
}
