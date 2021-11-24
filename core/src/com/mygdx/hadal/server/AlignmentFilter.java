package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.save.UnlockCharacter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * The AlignmentFilter corresponds to a chosen "team color" of each player which can be chosen at a hub event.
 * This color contains the filter necessary to set up team passability.
 * Team colors also contain a shader used for sprite pre-processing
 * @author Curnip Chuxley
 */
public enum AlignmentFilter {

    NONE(-3, HadalColor.NOTHING, HadalColor.NOTHING, false, ""),
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
    PLAYER11(-14),
    PLAYER12(-15),
    PLAYER13(-16),
    PLAYER14(-17),
    PLAYER15(-18),
    PLAYER16(-19),

    TEAM_BANANA(-25, HadalColor.BANANA, HadalColor.BEIGE, "BANANA"),
    TEAM_CELADON(-26, HadalColor.CELADON, HadalColor.GREEN, "CELADON"),
    TEAM_CHARTREUSE(-27, HadalColor.CHARTREUSE, HadalColor.PALE_GREEN, "CHARTREUSE"),
    TEAM_CRIMSON(-28, HadalColor.CRIMSON, HadalColor.RED, "CRIMSON"),
    TEAM_EGGPLANT(-29, HadalColor.EGGPLANT, HadalColor.GREEN, "EGGPLANT"),
    TEAM_GREY(-30, HadalColor.GREY, HadalColor.DARK_GREY, "GREY"),
    TEAM_PLUM(-31, HadalColor.PLUM, HadalColor.VIOLET, "PLUM"),
    TEAM_ORANGE(-32, HadalColor.ORANGE, HadalColor.GOLD, "ORANGE"),
    TEAM_SKY_BLUE(-33, HadalColor.SKY_BLUE, HadalColor.TURQOISE, "SKY BLUE"),
    TEAM_TAN(-34, HadalColor.TAN, HadalColor.BROWN, "TAN"),
    TEAM_TURQUIOSE(-35, HadalColor.TURQOISE, HadalColor.BLUE, "TURQUOISE"),
    TEAM_VIOLET(-36, HadalColor.VIOLET, HadalColor.BLUE, "VIOLET"),

    TEAM_BLACK_AND_WHITE(-40, HadalColor.GREY, HadalColor.GREY, "BLACK AND WHITE") {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/blackwhite.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_CENSURE(-41, HadalColor.NOTHING, HadalColor.NOTHING, false, "CENSURED") {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/censure.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_INVERT(-42, HadalColor.NOTHING, HadalColor.NOTHING, false, "INVERT") {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/invert.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_SEPIA(-43, HadalColor.NOTHING, HadalColor.NOTHING, "SEPIA") {

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
    private HadalColor color1 = HadalColor.NOTHING;
    private final Vector3 color1RGB = new Vector3();
    private final Vector3 color2RGB = new Vector3();

    private String adjective;

    //is this alignment currently being used? (this is for preventing users from having the same filter in free for all)
    private boolean used;

    //can this team be assigned randomly without anyone picking it? Set to false for the "weird" options
    private boolean standardChoice = true;

    AlignmentFilter(int filter) {
        this.filter = (short) filter;
        this.team = false;
    }

    AlignmentFilter(int filter, HadalColor color1, HadalColor color2, boolean standardChoice, String adjective) {
        this(filter, color1, color2, adjective);
        this.standardChoice = standardChoice;
    }

    AlignmentFilter(int filter, HadalColor color1, HadalColor color2, String adjective) {
        this.filter = (short) filter;
        this.team = true;
        this.color1 = color1;
        this.color1RGB.set(color1.getR(), color1.getG(), color1.getB());
        this.color2RGB.set(color2.getR(), color2.getG(), color2.getB());
        this.adjective = adjective;
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
        shader.setUniformf("newcolor1", color1RGB);
        shader.setUniformf("newcolor2", color2RGB);

        return shader;
    }

    //this array holds the current auto-assigned teams
    public static AlignmentFilter[] currentTeams = new AlignmentFilter[] {};

    //this array holds the scores of each auto-assigned teams
    public static int[] teamScores = new int[] {};

    /**
     * This is run by the server when a level loads with auto-assigned teams enabled
     * @param numTeams: how many teams to auto assign players to?
     */
    public static void autoAssignTeams(int numTeams, SettingTeamMode.TeamMode mode) {
        Array<User> users = new Array<>(HadalGame.server.getUsers().values().toArray());
        users.shuffle();

        int currentTeam = 0;

        currentTeams = new AlignmentFilter[numTeams];
        teamScores = new int[numTeams];
        Arrays.fill(currentTeams, AlignmentFilter.NONE);

        ObjectMap<User, Integer> teamSelection = new ObjectMap<>();

        //make all team colors usable
        for (AlignmentFilter filter: AlignmentFilter.values()) {
            if (filter.isTeam()) {
                filter.setUsed(false);
            }
        }

        //add each non-spectator to a team. If the player has a team color, it will be used as the team's color
        for (User user: users) {
            if (!user.isSpectator()) {
                if (mode.equals(SettingTeamMode.TeamMode.TEAM_AUTO)) {
                    teamSelection.put(user, currentTeam);

                    if (user.getTeamFilter() != AlignmentFilter.NONE && currentTeams[currentTeam] == AlignmentFilter.NONE) {
                        if (!user.getTeamFilter().isUsed() && user.getTeamFilter().standardChoice) {
                            user.getTeamFilter().setUsed(true);
                            currentTeams[currentTeam] = user.getTeamFilter();
                        }
                    }
                    currentTeam = (currentTeam + 1) % numTeams;
                } else if (mode.equals(SettingTeamMode.TeamMode.HUMANS_VS_BOTS)){
                    if (user.getScores().getConnID() < 0) {
                        currentTeam = 0;
                    } else {
                        currentTeam = 1;
                    }
                    teamSelection.put(user, currentTeam);
                    if (user.getTeamFilter() != AlignmentFilter.NONE && currentTeams[currentTeam] == AlignmentFilter.NONE) {
                        if (!user.getTeamFilter().isUsed() && user.getTeamFilter().standardChoice) {
                            user.getTeamFilter().setUsed(true);
                            currentTeams[currentTeam] = user.getTeamFilter();
                        }
                    }
                }
            }
        }

        //if any teams still lack colors, we give them a randomly generated one
        for (int i = 0; i < currentTeams.length; i++) {
            if (currentTeams[i] == AlignmentFilter.NONE) {
                Array<AlignmentFilter> unusedTeams = new Array<>();

                for (AlignmentFilter filter: AlignmentFilter.values()) {
                    if (filter.isTeam() && filter.standardChoice && !filter.isUsed()) {
                        unusedTeams.add(filter);
                    }
                }
                unusedTeams.shuffle();
                if (!unusedTeams.isEmpty()) {
                    currentTeams[i] = unusedTeams.get(0);
                    currentTeams[i].setUsed(true);
                }
            }
        }

        for (User user: teamSelection.keys()) {
            user.setTeamFilter(currentTeams[teamSelection.get(user)]);
        }
    }

    public static void resetTeams() {
        currentTeams = new AlignmentFilter[0];
        teamScores = new int[0];
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

    private final static float normalChance = 0.2f;
    private final static float colorChance = 0.9f;
    private static final AlignmentFilter[] colors = { TEAM_BANANA, TEAM_CELADON, TEAM_CHARTREUSE, TEAM_CRIMSON, TEAM_EGGPLANT,
            TEAM_GREY, TEAM_PLUM, TEAM_ORANGE, TEAM_SKY_BLUE, TEAM_TAN, TEAM_TURQUIOSE };
    private static final AlignmentFilter[] colorsWeird = { TEAM_BLACK_AND_WHITE, TEAM_CENSURE, TEAM_SEPIA, TEAM_INVERT };

    /**
     * Generates a random team color. used for bots. Normal and uncolored alignments are weighted more heavily
     * @return a random team color
     */
    public static AlignmentFilter getRandomColor() {
        float random = MathUtils.random();
        if (random < normalChance) {
            return NONE;
        }
        if (random < colorChance) {
            return colors[MathUtils.random(colors.length - 1)];
        }
        return colorsWeird[MathUtils.random(colorsWeird.length - 1)];
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

    public String getColoredAdjective() {
        return WeaponUtils.getColorName(color1, adjective);
    }

    public boolean isTeam() { return team; }

    public boolean isUsed() { return used; }

    public void setUsed(boolean used) { this.used = used; }

    public HadalColor getColor1() { return color1; }

    public Vector3 getColor1RGB() { return color1RGB; }

    private static final ObjectMap<String, AlignmentFilter> UnlocksByName = new ObjectMap<>();
    static {
        for (AlignmentFilter u: AlignmentFilter.values()) {
            UnlocksByName.put(u.toString(), u);
        }
    }
    public static AlignmentFilter getByName(String s) {
        return UnlocksByName.get(s, NONE);
    }
}
