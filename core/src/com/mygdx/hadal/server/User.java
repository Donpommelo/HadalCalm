package com.mygdx.hadal.server;

import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;

/**
 * A User represents a user playing the game, whether they are host or not.
 * This contains the data needed to keep track of the player's information (score, team alignment etc)
 * @author Brineflu Blemherst
 */
public class User {

    //player info and relevant entities
    private Player player;
    private MouseTracker mouse;
    private SavedPlayerFields scores;
    private SavedPlayerFieldsExtra scoresExtra;

    //has this player's score been updated? (used to sync score window)
    private boolean scoreUpdated;

    //is this player muted
    private boolean muted;

    //is the player a spectator?
    private boolean spectator;

    //player's hbox filter (for free for all pvp)
    private AlignmentFilter hitBoxFilter;
    private AlignmentFilter teamFilter = AlignmentFilter.NONE;

    public User(Player player, MouseTracker mouse, SavedPlayerFields scores, SavedPlayerFieldsExtra scoresExtra) {
        this.player = player;
        this.mouse = mouse;
        this.scores = scores;
        this.scoresExtra = scoresExtra;
        scoreUpdated = true;

        hitBoxFilter = AlignmentFilter.getUnusedAlignment();
    }

    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public MouseTracker getMouse() { return mouse; }

    public void setMouse(MouseTracker mouse) { this.mouse = mouse; }

    public SavedPlayerFields getScores() { return scores; }

    public void setScores(SavedPlayerFields scores) { this.scores = scores; }

    public SavedPlayerFieldsExtra getScoresExtra() { return scoresExtra; }

    public void setScoresExtra(SavedPlayerFieldsExtra scoresExtra) { this.scoresExtra = scoresExtra; }

    public boolean isScoreUpdated() { return scoreUpdated; }

    public void setScoreUpdated(boolean scoreUpdated) { this.scoreUpdated = scoreUpdated; }

    public boolean isMuted() { return muted; }

    public void setMuted(boolean muted) { this.muted = muted; }

    public boolean isSpectator() { return spectator; }

    public void setSpectator(boolean spectator) { this.spectator = spectator; }

    public AlignmentFilter getHitBoxFilter() { return hitBoxFilter; }

    public void setHitBoxFilter(AlignmentFilter hitBoxFilter) { this.hitBoxFilter = hitBoxFilter; }

    public AlignmentFilter getTeamFilter() { return teamFilter; }

    public void setTeamFilter(AlignmentFilter teamFilter) { this.teamFilter = teamFilter; }
}
