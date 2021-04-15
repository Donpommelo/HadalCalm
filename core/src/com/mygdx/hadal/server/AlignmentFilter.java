package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.save.UnlockCharacter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * The AlignmentFilter corresponds to a chosen "team color" of each player which can be chosen at a hub event.
 * This color contains the filter necessary to set up team passability.
 * Team colors also contain a shader used for sprite pre-processing
 * @author Curnip Chuxley
 */
public enum AlignmentFilter {

    NONE(-3, HadalColor.NOTHING, HadalColor.NOTHING, false),
    PLAYER1(-4),
    PLAYER2(-5),
    PLAYER3(-6),
    PLAYER4(-7),
    PLAYER5(-8),
    PLAYER6(-9),
    PLAYER7(-10),
    PLAYER8(-11),
    PLAYER9(-12),
    PLAYER10(-13),

    TEAM_CHARTREUSE(-16, HadalColor.CHARTREUSE, HadalColor.PALE_GREEN),
    TEAM_CRIMSON(-17, HadalColor.CRIMSON, HadalColor.RED),
    TEAM_GREY(-18, HadalColor.GREY, HadalColor.DARK_GREY),
    TEAM_PLUM(-19, HadalColor.PLUM, HadalColor.VIOLET),
    TEAM_ORANGE(-20, HadalColor.ORANGE, HadalColor.GOLD),
    TEAM_SKY_BLUE(-21, HadalColor.SKY_BLUE, HadalColor.TURQOISE),
    TEAM_TAN(-22, HadalColor.TAN, HadalColor.BROWN),

    TEAM_BLACK_AND_WHITE(-25, HadalColor.NOTHING, HadalColor.NOTHING) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/blackwhite.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_CENSURE(-26, HadalColor.NOTHING, HadalColor.NOTHING, false) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/censure.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_INVERT(-27, HadalColor.NOTHING, HadalColor.NOTHING, false) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/invert.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_SEPIA(-28, HadalColor.NOTHING, HadalColor.NOTHING) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/sepia.frag").readString());
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

    //can this team be assigned randomly without anyone picking it? Set to false for the "weird" options
    private boolean standardChoice = true;

    AlignmentFilter(int filter) {
        this.filter = (short) filter;
        this.team = false;
    }

    AlignmentFilter(int filter, HadalColor color1, HadalColor color2, boolean standardChoice) {
        this(filter, color1, color2);
        this.standardChoice = standardChoice;
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

    public static AlignmentFilter[] currentTeams = new AlignmentFilter[] {};
    public static int[] teamScores = new int[] {};
    public static void autoAssignTeams(int numTeams) {
        ArrayList<User> users = new ArrayList<>(HadalGame.server.getUsers().values());
        Collections.shuffle(users);

        int currentTeam = 0;

        currentTeams = new AlignmentFilter[numTeams];
        teamScores = new int[numTeams];
        Arrays.fill(currentTeams, AlignmentFilter.NONE);

        HashMap<User, Integer> teamSelection = new HashMap<>();

        //make all team colors usable
        for (AlignmentFilter filter: AlignmentFilter.values()) {
            if (filter.isTeam()) {
                filter.setUsed(false);
            }
        }

        for (User user: users) {
            if (!user.isSpectator()) {
                teamSelection.put(user, currentTeam);

                if (user.getTeamFilter() != AlignmentFilter.NONE && currentTeams[currentTeam] == AlignmentFilter.NONE) {
                    if (!user.getTeamFilter().isUsed()) {
                        user.getTeamFilter().setUsed(true);
                        currentTeams[currentTeam] = user.getTeamFilter();
                    }
                }
                currentTeam = (currentTeam + 1) % numTeams;
            }
        }

        for (int i = 0; i < currentTeams.length; i++) {
            if (currentTeams[i] == AlignmentFilter.NONE) {
                ArrayList<AlignmentFilter> unusedTeams = new ArrayList<>();

                for (AlignmentFilter filter: AlignmentFilter.values()) {
                    if (filter.isTeam() && filter.standardChoice && !filter.isUsed()) {
                        unusedTeams.add(filter);
                    }
                }
                Collections.shuffle(unusedTeams);
                if (!unusedTeams.isEmpty()) {
                    currentTeams[i] = unusedTeams.get(0);
                    currentTeams[i].setUsed(true);
                }
            }
        }

        HadalGame.server.sendToAllTCP(new Packets.SyncAssignedTeams(currentTeams));

        for (User user: teamSelection.keySet()) {
            user.setTeamFilter(currentTeams[teamSelection.get(user)]);
        }
    }

    /**
     * @return an unused alignment filter.
     * this is used when a new user is added to give them a unique "player number"
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

    /**
     * This makes all the teams usable again. It is called when a server is made.
     */
    public static void resetUsedAlignments() {
        for (AlignmentFilter filter: AlignmentFilter.values()) {
            filter.setUsed(false);
        }
    }

    public short getFilter() { return filter; }

    public boolean isTeam() { return team; }

    public boolean isUsed() { return used; }

    public void setUsed(boolean used) { this.used = used; }

    public Vector3 getColor1() { return color1; }

    private static final HashMap<String, AlignmentFilter> UnlocksByName = new HashMap<>();
    static {
        for (AlignmentFilter u: AlignmentFilter.values()) {
            UnlocksByName.put(u.toString(), u);
        }
    }
    public static AlignmentFilter getByName(String s) {
        return UnlocksByName.getOrDefault(s, NONE);
    }
}
