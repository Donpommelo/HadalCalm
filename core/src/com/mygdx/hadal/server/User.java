package com.mygdx.hadal.server;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

/**
 * A User represents a user playing the game, whether they are host or not.
 * This contains the data needed to keep track of the player's information (score, team alignment etc)
 * @author Brineflu Blemherst
 */
public class User {

    //player info and relevant entities
    private Player player;
    private final SavedPlayerFields scores;
    private final SavedPlayerFieldsExtra scoresExtra;

    //has this player's score been updated? (used to sync score window)
    private boolean scoreUpdated;

    //is this player muted
    private boolean muted;

    //is the player a spectator?
    private boolean spectator;

    //player's hbox filter (for free for all pvp)
    private AlignmentFilter hitBoxFilter;

    //the player's selected team alignment
    private AlignmentFilter teamFilter = AlignmentFilter.NONE;

    private TransitionState nextState;

    public User(Player player, SavedPlayerFields scores, SavedPlayerFieldsExtra scoresExtra) {
        this.player = player;
        this.scores = scores;
        this.scoresExtra = scoresExtra;
        scoreUpdated = true;

        hitBoxFilter = AlignmentFilter.getUnusedAlignment();
    }

    private float transitionTime;
    public void controller(PlayState state, float delta) {
        if (nextState != null) {
            transitionTime -= delta;
            if (transitionTime <= 0.0f) {
                if (nextState.equals(TransitionState.RESPAWN)) {
                    respawn(state);
                }
                nextState = null;
            }
        }
    }

    public void beginTransition(PlayState state, TransitionState nextState, boolean override, float fadeSpeed, float fadeDelay) {

        if (override || this.nextState == null) {
            this.nextState = nextState;
            this.transitionTime = fadeDelay + 1.0f / fadeSpeed;

            if (scores.getConnID() == 0) {
                if (override || state.getNextState() == null) {
                    state.beginTransition(nextState, fadeSpeed, fadeDelay);
                }
            } else {
                HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(nextState, fadeSpeed, fadeDelay));
            }
        }
    }

    private void respawn(PlayState state) {
        if (scores.getConnID() == 0) {
            StartPoint getSave = state.getSavePoint(this);

            //Create a new player
            short hitboxFilter = getHitBoxFilter().getFilter();
            state.setPlayer(state.createPlayer(getSave, state.getGsm().getLoadout().getName(), player.getPlayerData().getLoadout(),
                    player.getPlayerData(),0, true, false, hitboxFilter));

            state.getCamera().position.set(new Vector3(getSave.getStartPos().x, getSave.getStartPos().y, 0));
            state.getCameraFocusAim().set(getSave.getStartPos());

            ((PlayerController) state.getController()).setPlayer(player);
        } else {
            if (player != null) {
                //alive check prevents duplicate players if entering/respawning simultaneously
                if (!player.isAlive()) {
                    String playerName = player.getName();
                    HadalGame.server.createNewClientPlayer(state, scores.getConnID(), playerName,
                            player.getPlayerData().getLoadout(), player.getPlayerData(), true, false);
                }
            } else {
                //player is respawning from spectator and has no player
                HadalGame.server.createNewClientPlayer(state, scores.getConnID(), scores.getNameShort(), scoresExtra.getLoadout(),
                        null, true, false);
            }
        }
    }

    private static final Vector3 rgb = new Vector3();
    public String getNameAbridgedColored(int maxNameLen) {
        String displayedName = scores.getNameShort();

        if (displayedName.length() > maxNameLen) {
            displayedName = displayedName.substring(0, maxNameLen).concat("...");
        }

        if (teamFilter.getColor1RGB().isZero()) {
            rgb.setZero();
        } else {
            rgb.set(teamFilter.getColor1RGB());
        }

        String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
        return "[" + hex + "]" + displayedName + "[]";
    }

    /**
     * A UserDto is a data object used to send user info from server to client
     * This is sent upon going to results state to give clients accurate score information
     */
    public static class UserDto {

        public SavedPlayerFields scores;
        public SavedPlayerFieldsExtra scoresExtra;
        public boolean spectator;

        public UserDto() {}

        public UserDto(SavedPlayerFields scores, SavedPlayerFieldsExtra scoresExtra, boolean spectator) {
            this.scores = scores;
            this.scoresExtra = scoresExtra;
            this.spectator = spectator;
        }
    }

    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public SavedPlayerFields getScores() { return scores; }

    public SavedPlayerFieldsExtra getScoresExtra() { return scoresExtra; }

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
