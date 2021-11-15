package com.mygdx.hadal.server;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.packets.Packets;
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

    //the state this user is transitioning to (null if not transitioning)
    private TransitionState nextState;

    private StartPoint startPoint;

    private final Vector2 overrideSpawnLocation = new Vector2();
    private boolean spawnOverridden;

    public User(Player player, SavedPlayerFields scores, SavedPlayerFieldsExtra scoresExtra) {
        this.player = player;
        this.scores = scores;
        this.scoresExtra = scoresExtra;
        scoreUpdated = true;

        hitBoxFilter = AlignmentFilter.getUnusedAlignment();
    }

    private static final float spawnForewarn = 1.0f;
    private float transitionTime, transitionElapsed;
    private boolean spawnForewarned;
    public void controller(PlayState state, float delta) {

        //we keep track of each user's transition duration, so that we can make them respawn at the correct time
        if (nextState != null) {
            transitionTime -= delta;
            transitionElapsed += delta;

            //briefly before respawning, we want to flash particles at prospective spawn location
            if (transitionTime <= spawnForewarn && !spawnForewarned) {
                if (nextState.equals(TransitionState.RESPAWN)) {
                    spawnForewarned = true;
                    startPoint = state.getSavePoint(this);

                    new ParticleEntity(state, new Vector2(startPoint.getStartPos()).sub(0, startPoint.getSize().y / 2),
                            Particle.TELEPORT, 1.0f, true, ParticleEntity.particleSyncType.CREATESYNC);
                }
            }
            if (transitionTime <= 0.0f) {
                transitionElapsed = 0.0f;
                if (nextState.equals(TransitionState.RESPAWN)) {
                    respawn(state);
                }
                nextState = null;
            }
        }
    }

    /**
     * This is run when a user transitions to another state.
     * @param state: the play state
     * @param nextState: the transitionState they are following
     * @param override: does this change override an existing transition
     * @param fadeSpeed: the speed at which the screen will fade out
     * @param fadeDelay: the delay in seconds before the screen fades out
     */
    public void beginTransition(PlayState state, TransitionState nextState, boolean override, float fadeSpeed, float fadeDelay) {

        if (override || this.nextState == null) {
            this.nextState = nextState;
            this.transitionTime = fadeDelay + 1.0f / fadeSpeed;
            this.transitionElapsed = 0.0f;
            this.spawnForewarned = false;

            if (scores.getConnID() == 0) {
                if (override || state.getNextState() == null) {
                    state.beginTransition(nextState, fadeSpeed, fadeDelay);
                }
            } else {
                HadalGame.server.sendToTCP(scores.getConnID(), new Packets.ClientStartTransition(nextState, fadeSpeed, fadeDelay));
            }
        }
    }

    /**
     * This is run when a player respawns.
     * Create their new player character
     * @param state: the play state
     */
    public void respawn(PlayState state) {

        if (startPoint == null) {
            startPoint = state.getSavePoint(this);
        }

        if (scores.getConnID() == 0) {

            //Create a new player
            short hitboxFilter = getHitBoxFilter().getFilter();
            state.setPlayer(state.createPlayer(startPoint, state.getGsm().getLoadout().getName(), player.getPlayerData().getLoadout(),
                    player.getPlayerData(),0, this, true, false, hitboxFilter));

            if (!player.isDontMoveCamera()) {
                state.getCamera().position.set(new Vector3(startPoint.getStartPos().x, startPoint.getStartPos().y, 0));
                state.getCameraFocusAim().set(startPoint.getStartPos());
            }

            ((PlayerController) state.getController()).setPlayer(player);
        } else {
            if (player != null) {
                //alive check prevents duplicate players if entering/respawning simultaneously
                if (!player.isAlive()) {
                    String playerName = player.getName();
                    HadalGame.server.createNewClientPlayer(state, scores.getConnID(), playerName,
                            player.getPlayerData().getLoadout(), player.getPlayerData(), true, false, startPoint);
                }
            } else {
                //player is respawning from spectator and has no player
                HadalGame.server.createNewClientPlayer(state, scores.getConnID(), scores.getNameShort(), scoresExtra.getLoadout(),
                        null, true, false, startPoint);
            }
        }
    }

    private static final float spectatorDurationThreshold = 1.0f;
    public boolean isRespawnCameraSpectator() {
        return transitionElapsed > spectatorDurationThreshold;
    }

    /**
     * Run when entering a new level
     * This makes sure things like saved start points are reset
     */
    public void newLevelReset() {
        scores.newLevelReset();
        nextState = null;
        startPoint = null;
        spawnOverridden = false;
        transitionElapsed = 0.0f;
    }

    public void setOverrideSpawn(Vector2 overrideSpawn) {
        overrideSpawnLocation.set(overrideSpawn);
        spawnOverridden = true;
    }


    private static final Vector3 rgb = new Vector3();
    /**
     * This returns an abridged version of the user's name
     * Additionally, the name will be colored according to the user's alignment
     * @param maxNameLen: Max length of name. Any more will be abridged with ellipses
     * @return the modified name
     */
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

    public Vector2 getOverrideSpawnLocation() { return overrideSpawnLocation; }

    public boolean isSpawnOverridden() { return spawnOverridden; }
}
