package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.ColorPalette;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.text.HText;

import java.util.Arrays;

/**
 * The AlignmentFilter corresponds to a chosen "team color" of each player which can be chosen at a hub event.
 * This color contains the filter necessary to set up team passability.
 * Team colors also contain a shader used for sprite pre-processing
 * @author Curnip Chuxley
 */
public enum AlignmentFilter {

    NONE(-3, ColorPalette.BASE, false, HText.NOTHING),
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

    TEAM_BANANA(-25, ColorPalette.BANANA, HText.BANANA, HadalColor.YELLOW),
    TEAM_CELADON(-26, ColorPalette.CELADON, HText.CELADON, HadalColor.GREEN),
    TEAM_CHARTREUSE(-27, ColorPalette.CHARTREUSE, HText.CHARTREUSE, HadalColor.GREEN),
    TEAM_COQUELICOT(-28, ColorPalette.COQUELICOT, HText.COQUELICOT, HadalColor.RED),
    TEAM_CRIMSON(-29, ColorPalette.CRIMSON, HText.CRIMSON, HadalColor.RED),
    TEAM_EGGPLANT(-30, ColorPalette.EGGPLANT, HText.EGGPLANT, HadalColor.VIOLET),
    TEAM_GOLD(-31, ColorPalette.GOLD, HText.GOLD, HadalColor.ORANGE, HadalColor.YELLOW),
    TEAM_GREY(-32, ColorPalette.GREY, HText.GREY, HadalColor.GREY, HadalColor.BROWN),
    TEAM_PLUM(-33, ColorPalette.PLUM, HText.PLUM, HadalColor.VIOLET),
    TEAM_MAUVE(-34, ColorPalette.MAUVE, HText.MAUVE, HadalColor.VIOLET, HadalColor.BROWN),
    TEAM_ORANGE(-35, ColorPalette.ORANGE, HText.ORANGE, HadalColor.ORANGE, HadalColor.RED),
    TEAM_SKY_BLUE(-36, ColorPalette.SKY_BLUE, HText.SKY_BLUE, HadalColor.BLUE),
    TEAM_TAN(-37, ColorPalette.TAN, HText.TAN, HadalColor.BROWN, HadalColor.YELLOW),
    TEAM_TURQUOISE(-38, ColorPalette.TURQUOISE, HText.TURQUOISE, HadalColor.BLUE, HadalColor.GREEN),
    TEAM_VIOLET(-39, ColorPalette.VIOLET, HText.VIOLET, HadalColor.VIOLET),

    TEAM_BLACK_AND_WHITE(-40, ColorPalette.BASE, HText.BLACK_AND_WHITE) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/blackwhite.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_CENSURE(-41, ColorPalette.BASE, false, HText.CENSURED) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/censure.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_INVERT(-42, ColorPalette.BASE, false, HText.INVERT) {

        @Override
        public ShaderProgram getShader(UnlockCharacter character) {
            ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/invert.frag").readString());
            shader.bind();
            return shader;
        }
    },

    TEAM_SEPIA(-43, ColorPalette.BASE, false, HText.SEPIA) {

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
    private final ColorPalette palette;

    //color group are a "similar" colors that are used to prevent teams from having similar palettes
    private HadalColor[] colorGroup = {};

    //this string describes the team for purposes like flag-capture notifications
    private String adjective;

    //is this alignment currently being used? (this is for preventing users from having the same filter in free for all)
    private boolean used;

    //can this team be assigned randomly without anyone picking it? Set to false for the "weird" options
    private boolean standardChoice = true;

    AlignmentFilter(int filter) {
        this.filter = (short) filter;
        this.team = false;
        this.palette = ColorPalette.BASE;
    }

    AlignmentFilter(int filter, ColorPalette palette, boolean standardChoice, HText adjective) {
        this(filter, palette, adjective);
        this.standardChoice = standardChoice;
    }

    AlignmentFilter(int filter, ColorPalette palette, HText adjective, HadalColor... colorGroup) {
        this.filter = (short) filter;
        this.palette = palette;
        this.adjective = adjective.text();
        this.colorGroup = colorGroup;
        this.team = true;
    }

    /**
     * For alignments with a preprocessing shader, this returns that shader.
     * For alignments with non-color-replacing shaders, override this method.
     * @param character: the character skin this shader is applied to.
     * @return a shader for the input character
     */
    public ShaderProgram getShader(UnlockCharacter character) {
        return palette.getShader(character);
    }

    //this array holds the current auto-assigned teams
    public static AlignmentFilter[] currentTeams = new AlignmentFilter[] {};

    //this array holds the scores of each auto-assigned teams
    public static int[] teamScores = new int[] {};

    /**
     * This is run by the server when a level loads with auto-assigned teams enabled
     * @param numTeams: how many teams to auto assign players to?
     * @param mode: mode is used to determine if any special team-assignment rules are needed (like humans vs bots)
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
        for (AlignmentFilter filter : AlignmentFilter.values()) {
            if (filter.isTeam()) {
                filter.setUsed(false);
            }
        }

        //add each non-spectator to a team.
        for (User user : users) {
            if (!user.isSpectator()) {
                if (mode.equals(TeamMode.TEAM_AUTO)) {
                    currentTeam = (currentTeam + 1) % numTeams;
                    teamSelection.put(user, currentTeam);
                } else if (mode.equals(TeamMode.HUMANS_VS_BOTS)){
                    if (user.getScores().getConnID() < 0) {
                        currentTeam = 0;
                    } else {
                        currentTeam = 1;
                    }
                    teamSelection.put(user, currentTeam);
                }
            }
        }

        //give each team a randomly generated team color
        for (int i = 0; i < currentTeams.length; i++) {
            Array<AlignmentFilter> unusedTeams = new Array<>();

            //iterate over all team colors. Make sure we do not choose a team similar to any other current team colors
            for (AlignmentFilter filter : AlignmentFilter.values()) {
                if (!filter.isUsed() && filter.team && filter.standardChoice) {
                    boolean similar = false;
                    for (AlignmentFilter alignmentFilter : currentTeams) {
                        for (HadalColor group1 : alignmentFilter.colorGroup) {
                            for (HadalColor group2 : filter.colorGroup) {
                                if (group1 == group2) {
                                    similar = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!similar) {
                        unusedTeams.add(filter);
                    }
                }
            }
            unusedTeams.shuffle();
            if (!unusedTeams.isEmpty()) {
                currentTeams[i] = unusedTeams.get(0);
                currentTeams[i].setUsed(true);
            }
        }

        for (User user : teamSelection.keys()) {
            user.setTeamFilter(currentTeams[teamSelection.get(user)]);
        }
    }

    /**
     * Run when a player connects to an ongoing game that they are allowed to join
     * @param newUser: the player that just connected
     */
    public static void assignNewPlayerToTeam(User newUser) {

        //first, we keep track of how many players are on each team
        ObjectMap<AlignmentFilter, Integer> teamSelection = new ObjectMap<>();

        //we add current teams so that new players can be assigned to empty teams
        for (AlignmentFilter team : currentTeams) {
            teamSelection.put(team, 0);
        }

        for (User user : HadalGame.server.getUsers().values()) {
            if (!user.isSpectator() && !user.equals(newUser)) {
                teamSelection.put(user.getTeamFilter(), teamSelection.get(user.getTeamFilter(), 0) + 1);
            }
        }

        //then we add the newly connected player to the team with the fewest players
        int minNumber = -1;
        AlignmentFilter smallestTeam = null;
        for (ObjectMap.Entry<AlignmentFilter, Integer> team : teamSelection.entries()) {
            if (minNumber == -1 || team.value < minNumber) {
                minNumber = team.value;
                smallestTeam = team.key;
            }
        }

        if (smallestTeam != null) {
            newUser.setTeamFilter(smallestTeam);
        }
    }

    /**
     * upon starting a new match, teams are reset so last match's teams are cleared
     */
    public static void resetTeams() {
        currentTeams = new AlignmentFilter[0];
        teamScores = new int[0];
    }

    /**
     * @return an unused alignment filter.
     * this is used when a new user is added to give them a unique "player number"
     */
    public static AlignmentFilter getUnusedAlignment() {
        for (AlignmentFilter filter : AlignmentFilter.values()) {
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
            TEAM_GREY, TEAM_PLUM, TEAM_ORANGE, TEAM_SKY_BLUE, TEAM_TAN, TEAM_TURQUOISE};
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
        for (AlignmentFilter filter : AlignmentFilter.values()) {
            filter.setUsed(false);
        }
    }

    public ColorPalette getPalette() { return palette; }

    public short getFilter() { return filter; }

    public String getTeamName() { return HText.TEAM + " " + adjective; }

    public String getColoredAdjective() {
        return WeaponUtils.getColorName(palette.getIcon(), adjective);
    }

    public boolean isTeam() { return team; }

    public boolean isUsed() { return used; }

    public void setUsed(boolean used) { this.used = used; }

    private static final ObjectMap<String, AlignmentFilter> UnlocksByName = new ObjectMap<>();
    static {
        for (AlignmentFilter u : AlignmentFilter.values()) {
            UnlocksByName.put(u.toString(), u);
        }
    }
    public static AlignmentFilter getByName(String s) {
        return UnlocksByName.get(s, NONE);
    }
}
