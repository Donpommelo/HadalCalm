package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

import java.util.HashMap;
import java.util.Map;

public class FrameBufferManager {

    private static final HashMap<CharacterColor, FrameBuffer> FRAME_BUFFER = new HashMap<>();

    public static FrameBuffer getFrameBuffer(SpriteBatch batch, UnlockCharacter character, AlignmentFilter team) {

        FrameBuffer fbo = FRAME_BUFFER.get(new CharacterColor(character, team));
        if (null != fbo) {
            return fbo;
        }

        //obtain new texture and create new frame buffer object
        Texture tex = character.getTexture();

        fbo = new FrameBuffer(Pixmap.Format.RGBA4444, tex.getWidth(), tex.getHeight(), false);
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

        FRAME_BUFFER.put(new CharacterColor(character, team), fbo);

        return fbo;
    }

    /**
     * This is called from the hub menus to dispose of fbo used for cosmetic previews
     * We do this because we don't want every fbo to be saved when browsing the menu
     * We can't call this at the start of the level because loadouts aren't set until player creation
     */
    public static void clearUnusedFrameBuffers(PlayState state) {
        Array<User> users;
        if (state.isServer()) {
            users = HadalGame.server.getUsers().values().toArray();
        } else {
            users = HadalGame.client.getUsers().values().toArray();
        }

        Array<Map.Entry<CharacterColor, FrameBuffer>> fboToRemove = new Array<>();
        for (Map.Entry<CharacterColor, FrameBuffer> characterBuffers : FRAME_BUFFER.entrySet()) {
            boolean used = false;

            for (User user : users) {
                if (null != user.getPlayer() && null != user.getPlayer().getPlayerData().getLoadout()) {
                    if (user.getPlayer().getPlayerData().getLoadout().character == characterBuffers.getKey().getCharacter()
                            && user.getPlayer().getPlayerData().getLoadout().team == characterBuffers.getKey().getTeam()) {
                        used = true;
                        break;
                    }
                }
            }

            if (!used) {
                fboToRemove.add(characterBuffers);
            }
        }

        for (Map.Entry<CharacterColor, FrameBuffer> fbo : fboToRemove) {
            fbo.getValue().dispose();
            FRAME_BUFFER.remove(fbo.getKey());
        }
    }

    /**
     * This is called at the start of levels or when the game is ended.
     * This clears all existing fbos to clear up memory
     */
    public static void clearAllFrameBuffers() {
        for (FrameBuffer fbo : FRAME_BUFFER.values()) {
            fbo.dispose();
        }
        FRAME_BUFFER.clear();
    }

    public record CharacterColor(UnlockCharacter character, AlignmentFilter team) {
        public UnlockCharacter getCharacter() { return character; }
        public AlignmentFilter getTeam() { return team; }
    }
}
