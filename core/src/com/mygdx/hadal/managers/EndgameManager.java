package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.managers.SkinManager.FONT_UI;

public class EndgameManager {

    protected final PlayState state;

    //If we are transitioning to a results screen, this is the displayed text;
    protected String resultsText = "";

    public EndgameManager(PlayState state) {
        this.state = state;
    }

    //This is a list of all the saved player fields (scores) from the completed playstate
    private final ObjectMap<AlignmentFilter, Integer> teamKills = new ObjectMap<>();
    private final ObjectMap<AlignmentFilter, Integer> teamDeaths = new ObjectMap<>();
    private final ObjectMap<AlignmentFilter, Integer> teamScores = new ObjectMap<>();
    private final Array<AlignmentFilter> teamScoresList = new Array<>();

    //this is used to avoid running this multiple times
    private boolean levelEnded;
    /**
     * This is called when a level ends. Only called by the server. Begin a transition and tell all clients to follow suit.
     * @param text: text displayed in results state
     * @param victory: indicates a win for all players in pve
     * @param incrementWins: Should player's wins be incremented? (false for arcade break room)
     * @param fadeDelay: Duration of
     */
    public void levelEnd(String text, boolean victory, boolean incrementWins, float fadeDelay) {
        if (levelEnded) { return; }
        levelEnded = true;

        String resultsText = text;

        //list of non-spectator users to be sorted
        Array<User> activeUsers = new Array<>();
        for (User user : HadalGame.usm.getUsers().values().toArray()) {
            if (!user.isSpectator()) {
                activeUsers.add(user);
            }
        }

        //magic word indicates that we generate the results text dynamically based on score
        if (ResultsState.MAGIC_WORD.equals(text)) {
            for (User user : HadalGame.usm.getUsers().values().toArray()) {
                if (!user.isSpectator()) {

                    AlignmentFilter faction;
                    if (AlignmentFilter.NONE.equals(user.getTeamFilter())) {
                        faction = user.getHitboxFilter();
                    } else {
                        faction = user.getTeamFilter();
                    }

                    //add users's kills, deaths and scores to respective list and keep track of sum
                    if (teamKills.containsKey(faction)) {
                        teamKills.put(faction, teamKills.get(faction) + user.getScoreManager().getKills());
                    } else {
                        teamKills.put(faction, user.getScoreManager().getKills());
                    }
                    if (teamDeaths.containsKey(faction)) {
                        teamDeaths.put(faction, teamDeaths.get(faction) + user.getScoreManager().getDeaths());
                    } else {
                        teamDeaths.put(faction, user.getScoreManager().getDeaths());
                    }
                    if (teamScores.containsKey(faction)) {
                        teamScores.put(faction, teamScores.get(faction) + user.getScoreManager().getScore());
                    } else {
                        teamScores.put(faction, user.getScoreManager().getScore());
                    }
                }
            }

            //sort scores and team scores according to score, then kills, then deaths
            activeUsers.sort((a, b) -> {
                int cmp = (b.getScoreManager().getScore() - a.getScoreManager().getScore());
                if (cmp == 0) { cmp = b.getScoreManager().getKills() - a.getScoreManager().getKills(); }
                if (cmp == 0) { cmp = a.getScoreManager().getDeaths() - b.getScoreManager().getDeaths(); }
                return cmp;
            });

            teamScoresList.addAll(teamScores.keys().toArray());
            teamScoresList.sort((a, b) -> {
                int cmp = (teamScores.get(b) - teamScores.get(a));
                if (cmp == 0) { cmp = teamKills.get(b) - teamKills.get(a); }
                if (cmp == 0) { cmp = teamDeaths.get(a) - teamDeaths.get(b); }
                return cmp;
            });

            //if free-for-all, the first player in the sorted list is the victor
            if (SettingTeamMode.TeamMode.FFA.equals(state.getMode().getTeamMode())) {
                resultsText = UIText.PLAYER_WINS.text(activeUsers.get(0).getStringManager().getNameShort());
            } else {

                //in team modes, get the winning team and display a win for that team (or individual if no alignment)
                AlignmentFilter winningTeam = teamScoresList.get(0);
                if (winningTeam.isTeam()) {
                    resultsText = UIText.PLAYER_WINS.text(winningTeam.getColoredAdjective());
                } else {
                    for (User user : activeUsers) {
                        if (user.getHitboxFilter().equals(winningTeam)) {
                            resultsText = UIText.PLAYER_WINS.text(user.getStringManager().getNameShort());
                        }
                    }
                }
            }

            AlignmentFilter winningTeam = teamScoresList.get(0);
            ScoreManager winningScore = activeUsers.get(0).getScoreManager();

            //give a win to all players with a winning alignment (team or solo)
            for (User user : activeUsers) {
                ScoreManager score = user.getScoreManager();
                if (SettingTeamMode.TeamMode.FFA.equals(state.getMode().getTeamMode())) {
                    if (score.getScore() == winningScore.getScore() && score.getKills() == winningScore.getKills()
                            && score.getDeaths() == winningScore.getDeaths()) {
                        if (incrementWins) {
                            score.win();
                        }
                    }
                } else {
                    AlignmentFilter faction;

                    if (AlignmentFilter.NONE.equals(user.getTeamFilter())) {
                        faction = user.getHitboxFilter();
                    } else {
                        faction = user.getTeamFilter();
                    }

                    if (teamScores.containsKey(faction)) {
                        score.setTeamScore(teamScores.get(faction));
                    }

                    if (user.getHitboxFilter().equals(winningTeam) || user.getTeamFilter().equals(winningTeam)) {
                        if (incrementWins) {
                            score.win();
                        }
                    }
                }
            }
        } else if (victory) {
            //in coop, all players get a win if the team wins
            if (incrementWins) {
                for (User user : activeUsers) {
                    user.getScoreManager().win();
                }
            }
        }

        if (SettingArcade.arcade) {
            SettingArcade.processEndOfRound(state, state.getMode());
        } else {
            transitionToResultsState(resultsText, fadeDelay);
        }
    }

    /**
     * This is run by the server to transition to the results screen
     * @param resultsText: what text to display as the title of the results screen?
     * @param fadeDelay: how many seconds of delay before transition begins?
     */
    public void transitionToResultsState(String resultsText, float fadeDelay) {

        //mode-specific end-game processing (Atm this just cleans up bot pathfinding threads)
        state.getMode().processGameEnd(state);

        this.resultsText = resultsText;

        //create list of user information to send to all clients
        User.UserDto[] users = new User.UserDto[HadalGame.usm.getUsers().size];

        int userIndex = 0;
        for (User user : HadalGame.usm.getUsers().values()) {
            users[userIndex] = new User.UserDto(user.getScoreManager(), user.getStatsManager(), user.getLoadoutManager().getActiveLoadout(),
                    user.getStringManager().getName(), user.getConnID(), user.getPing(), user.isSpectator());
            userIndex++;
        }
        PacketManager.serverTCPAll(new Packets.SyncExtraResultsInfo(users, resultsText));

        //all users transition to results state
        for (User user : HadalGame.usm.getUsers().values()) {
            user.getTransitionManager().beginTransition(state,
                    new Transition()
                            .setNextState(TransitionState.RESULTS)
                            .setFadeSpeed(0.0f)
                            .setFadeDelay(fadeDelay)
                            .setOverride(true));
        }
    }

    private static final float END_TEXT_Y = 700;
    private static final float END_TEXT_WIDTH = 400;
    private static final float END_TEXT_SCALE = 2.5f;
    /**
     * @return a snapshot of the player's current perspective. Used for transitioning to results state
     */
    public FrameBuffer resultsStateFreeze(SpriteBatch batch) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA4444, (int) HadalGame.CONFIG_WIDTH, (int) HadalGame.CONFIG_HEIGHT, false);
        fbo.begin();

        //clear buffer, set camera
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

        state.render(0.0f);

        //draw extra ui elements for snapshot
        batch.setProjectionMatrix(state.getHud().combined);
        batch.begin();
        FONT_UI.getData().setScale(END_TEXT_SCALE);
        FONT_UI.draw(batch, UIText.GAME.text(),HadalGame.CONFIG_WIDTH / 2 - END_TEXT_WIDTH / 2, END_TEXT_Y, END_TEXT_WIDTH,
                Align.center, true);
        batch.end();

        fbo.end();

        return fbo;
    }

    public String getResultsText() { return resultsText; }
    public void setResultsText(String resultsText) { this.resultsText = resultsText; }

    public void setLevelEnded(boolean levelEnded) { this.levelEnded = levelEnded; }
}
