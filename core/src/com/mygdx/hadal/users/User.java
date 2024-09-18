package com.mygdx.hadal.users;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;

/**
 * A User represents a user playing the game, whether they are host or not.
 * This contains the data needed to keep track of the player's information (score, team alignment etc)
 * @author Brineflu Blemherst
 */
public class User {

    private final int connID;
    private int ping;

    //player info and relevant score information
    private Player player;

    //managers keep track of loadout, name and transition state respectively
    private final LoadoutManager loadoutManager;
    private final StringManager stringManager;
    private final UserTransitionManager transitionManager;
    private final EffectManager effectManager;

    //Keeps track of player's score as well as in-game stats
    private ScoreManager scoreManager;
    private StatsManager statsManager;

    //has this player's score been updated? (used to sync score window)
    private boolean scoreUpdated;

    //is this player muted?
    private boolean muted;

    //is the player a spectator?
    private boolean spectator;

    //player's hbox filter (for free for all pvp)
    private AlignmentFilter hitboxFilter;

    //the player's selected team alignment
    private AlignmentFilter teamFilter;

    //has this user been assigned to a team yet? Used so that new player connecting during match is assigned to a team.
    private boolean teamAssigned;

    public User(int connID, String name, Loadout loadout) {
        this.connID = connID;
        this.loadoutManager = new LoadoutManager(loadout);
        this.scoreManager = new ScoreManager();
        this.statsManager = new StatsManager();
        this.stringManager = new StringManager(this, name);
        this.transitionManager = new UserTransitionManager(this);
        this.effectManager = new EffectManager(this);
        scoreUpdated = true;

        hitboxFilter = AlignmentFilter.getUnusedAlignment();
        teamFilter = loadout.team;
    }

    /**
     * User controller is run in PlayState and keeps track of things like respawn transitions
     */
    public void controller(PlayState state, float delta) {
        transitionManager.controller(state, delta);
    }

    /**
     * Run when entering a new level
     * This makes sure things like saved start points, score, stats are reset
     */
    public void newLevelReset(PlayState state) {
        transitionManager.newLevelReset();
        scoreManager.newLevelReset(state);
        statsManager.newLevelReset();
        effectManager.newLevelReset();
    }

    public void afterPlayerCreate(Player player) {
        effectManager.afterPlayerCreate(player);
    }

    /**
     * A UserDto is a data object used to send user info from server to client
     * This is sent upon going to results state to give clients accurate score information
     */
    public static class UserDto {

        public ScoreManager scores;
        public StatsManager stats;
        public Loadout loadout;
        public String name;
        public int connID, ping;
        public boolean spectator;

        public UserDto() {}

        public UserDto(ScoreManager scores, StatsManager stats, Loadout loadout, String name, int connID, int ping, boolean spectator) {
            this.scores = scores;
            this.stats = stats;
            this.loadout = loadout;
            this.name = name;
            this.connID = connID;
            this.ping = ping;
            this.spectator = spectator;
        }
    }


    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public int getPing() { return ping; }

    public void setPing(int ping) { this.ping = ping; }

    public int getConnID() { return connID; }

    public ScoreManager getScoreManager() { return scoreManager; }

    public void setScoreManager(ScoreManager scoreManager) { this.scoreManager = scoreManager; }

    public StatsManager getStatsManager() { return statsManager; }

    public void setStatsManager(StatsManager statsManager) { this.statsManager = statsManager; }

    public LoadoutManager getLoadoutManager() { return loadoutManager; }

    public StringManager getStringManager() { return stringManager; }

    public UserTransitionManager getTransitionManager() { return transitionManager; }

    public EffectManager getEffectManager() { return effectManager; }

    public boolean isScoreUpdated() { return scoreUpdated; }

    public void setScoreUpdated(boolean scoreUpdated) { this.scoreUpdated = scoreUpdated; }

    public boolean isMuted() { return muted; }

    public void setMuted(boolean muted) { this.muted = muted; }

    public boolean isSpectator() { return spectator; }

    public void setSpectator(boolean spectator) { this.spectator = spectator; }

    public AlignmentFilter getHitboxFilter() { return hitboxFilter; }

    public void setHitboxFilter(AlignmentFilter hitboxFilter) { this.hitboxFilter = hitboxFilter; }

    public AlignmentFilter getTeamFilter() { return teamFilter; }

    public void setTeamFilter(AlignmentFilter teamFilter) { this.teamFilter = teamFilter; }

    public boolean isTeamAssigned() { return teamAssigned; }

    public void setTeamAssigned(boolean teamAssigned) { this.teamAssigned = teamAssigned; }
}
