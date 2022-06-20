package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.server.AlignmentFilter;

import static com.mygdx.hadal.effects.CharacterCosmetic.cosmeticAnimationSpeed;

/**
 * A ShadedSprite is a sprite + shader combination
 * This keeps track of the needed fbos, so they can be disposed of
 * @author Bireau Bunkydory
 */
public class ShadedSprite {

    //the fbos used to draw the sprites. These must be disposed of when we are done with the sprite
    private final Array<FrameBuffer> fbo = new Array<>();

    //The list of sprites drawn from fbos and animation composed of those sprites
    private final Array<TextureRegion> sprite = new Array<>();
    private Animation<TextureRegion> animation;

    public ShadedSprite(Batch batch, AlignmentFilter team, UnlockCharacter character, TextureRegion[] sprites) {
        this(batch, team, character, sprites, Animation.PlayMode.LOOP);
    }

    public ShadedSprite(Batch batch, AlignmentFilter team, UnlockCharacter character, TextureRegion[] sprites, Animation.PlayMode mode) {
        ShaderProgram shader;
        if (team.isTeam() && team != AlignmentFilter.NONE) {
            shader = team.getShader(character);
        } else {
            shader = character.getPalette().getShader(character);
        }
        createSprite(batch, shader, sprites, mode);
    }

    /**
     * This draws each sprite from respective fbo
     */
    private void createSprite(Batch batch, ShaderProgram shader, TextureRegion[] sprites, Animation.PlayMode mode) {
        for (TextureRegion tex : sprites) {
            FrameBuffer frame = new FrameBuffer(Pixmap.Format.RGBA4444, tex.getRegionWidth(), tex.getRegionHeight(), true);
            frame.begin();

            //clear buffer, set camera
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.getProjectionMatrix().setToOrtho2D(0, 0, frame.getWidth(), frame.getHeight());

            batch.begin();
            batch.setShader(shader);

            batch.draw(tex, 0, 0);

            if (shader != null) {
                batch.setShader(null);
            }
            batch.end();
            frame.end();

            TextureRegion fboRegion = new TextureRegion(frame.getColorBufferTexture());
            sprite.add(new TextureRegion(fboRegion, fboRegion.getRegionX(), fboRegion.getRegionHeight() - fboRegion.getRegionY(),
                    fboRegion.getRegionWidth(), - fboRegion.getRegionHeight()));
            fbo.add(frame);
        }

        if (shader != null) {
            shader.dispose();
        }

        animation = new Animation<>(cosmeticAnimationSpeed, sprite);
        animation.setPlayMode(mode);
    }

    /**
     * This disposes of each fbo and must be run when the sprite is deleted
     */
    public void dispose() {
        for (FrameBuffer frame : fbo) {
            frame.dispose();
        }
    }

    public Animation<TextureRegion> getAnimation() { return animation; }

    public TextureRegion getSprite() { return sprite.get(0); }
}
