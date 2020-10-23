package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.save.UnlockCharacter;

/**
 * The AlignmentFilter corresponds to a chosen "team color" of each player which can be chosen at a hub event.
 * This color contains the filter necessary to set up team passability.
 * Team colors also contain a shader used for sprite pre-processing
 * @author Curnip Chuxley
 */
public enum AlignmentFilter {

    NONE(-3, HadalColor.NOTHING, HadalColor.NOTHING),
    PLAYER1(-4),
    PLAYER2(-5),
    PLAYER3(-6),
    PLAYER4(-7),
    PLAYER5(-8),
    PLAYER6(-9),
    PLAYER7(-10),
    PLAYER8(-11),

    TEAM_CHARTREUSE(-13, HadalColor.CHARTREUSE, HadalColor.GREEN),
    TEAM_PLUM(-14, HadalColor.PLUM, HadalColor.VIOLET),
    TEAM_ORANGE(-14, HadalColor.ORANGE, HadalColor.GOLD),
    TEAM_RED(-12, HadalColor.RED, HadalColor.HOT_PINK),
    TEAM_SKY_BLUE(-15, HadalColor.SKY_BLUE, HadalColor.INDIGO),
    TEAM_SEPIA(-16, HadalColor.NOTHING, HadalColor.NOTHING) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/sepia.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_INVERT(-17, HadalColor.NOTHING, HadalColor.NOTHING) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/invert.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_CENSURE(-18, HadalColor.NOTHING, HadalColor.NOTHING) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/censure.frag").readString());
            shader.bind();
            return shader;
        }
    },
    ;

    //the hitbox filter associated with this filter
    private final short filter;

    //is this a team alignment or single player?
    private final boolean team;

    //for color-changing alignments, these represent the primary and secondary colors of the palette
    private final Vector3 color1 = new Vector3();
    private final Vector3 color2 = new Vector3();

    //is this alignment currently being used? (this is for preventing users from having the same filter in free for all)
    private boolean used;

    AlignmentFilter(int filter) {
        this.filter = (short) filter;
        this.team = false;
    }

    AlignmentFilter(int filter, HadalColor color1, HadalColor color2) {
        this.filter = (short) filter;
        this.team = true;
        this.color1.set(color1.getR(), color1.getG(), color1.getB());
        this.color2.set(color2.getR(), color2.getG(), color2.getB());
    }

    /**
     * For alignments with a preprocessing shader, this returns that shader.
     * For alignments with non-color-replacing shaders, override this method.
     * @param character: the character skin this shader is applied to.
     * @return a shader for the input character
     */
    public ShaderProgram getShader(UnlockCharacter character) {
        ShaderProgram shader = new ShaderProgram(
            Gdx.files.internal("shaders/pass.vert").readString(),
            Gdx.files.internal("shaders/colorreplace.frag").readString());

        shader.bind();
        shader.setUniformf("oldcolor1", character.getColor1());
        shader.setUniformf("oldcolor2", character.getColor2());
        shader.setUniformf("newcolor1", color1);
        shader.setUniformf("newcolor2", color2);

        return shader;
    }

    /**
     * @return an unused alignment filter.
     */
    public static AlignmentFilter getUnusedAlignment() {
        for (AlignmentFilter filter: AlignmentFilter.values()) {
            if (!filter.isUsed() && !filter.isTeam()) {
                filter.setUsed(true);
                return filter;
            }
        }
        return NONE;
    }

    public short getFilter() { return filter; }

    public boolean isTeam() { return team; }

    public boolean isUsed() { return used; }

    public void setUsed(boolean used) { this.used = used; }
}
