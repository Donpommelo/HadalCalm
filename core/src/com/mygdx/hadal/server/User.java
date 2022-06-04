package com.mygdx.hadal.server;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

/**
 * A User represents a user playing the game, whether they are host or not.
 * This contains the data needed to keep track of the player's information (score, team alignment etc)
 * @author Brineflu Blemherst
 */
public class User {

    //player info and relevant score information
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

    //player's primary weapon in their last saved loadout. Only used for clients for the effect of a single artifact
    private UnlockEquip lastEquippedPrimary = UnlockEquip.NOTHING;

    //the state this user is transitioning to (null if not transitioning)
    private TransitionState nextState;

    //the start point this user will respawn at next. Used to draw particles at the point prior to respawning
    private Event startPoint;

    //used when the player is spawned at a set location instead of using a start point (for matryoshka mode instant repawn)
    private final Vector2 overrideSpawnLocation = new Vector2();
    private boolean spawnOverridden, startOverridden;

    public User(Player player, SavedPlayerFields scores, SavedPlayerFieldsExtra scoresExtra) {
        this.player = player;
        this.scores = scores;
        this.scoresExtra = scoresExtra;
        scoreUpdated = true;

        hitBoxFilter = AlignmentFilter.getUnusedAlignment();
    }

    private static final float spawnForewarn = 2.0f;
    private float transitionTime;
    private boolean spawnForewarned;
    public void controller(PlayState state, float delta) {

        //we keep track of each user's transition duration, so that we can make them respawn at the correct time
        if (nextState != null) {
            transitionTime -= delta;

            //briefly before respawning, we want to flash particles at prospective spawn location
            if (transitionTime <= spawnForewarn && !spawnForewarned) {
                if (nextState.equals(TransitionState.RESPAWN)) {
                    spawnForewarned = true;

                    if (!startOverridden) {
                        startPoint = state.getSavePoint(this);
                    }

                    if (spawnOverridden) {
                        new ParticleEntity(state, new Vector2(overrideSpawnLocation).sub(0, startPoint.getSize().y),
                                Particle.TELEPORT_PRE, spawnForewarn, true, SyncType.CREATESYNC);
                    } else {
                        new ParticleEntity(state, new Vector2(startPoint.getStartPos()).sub(0, startPoint.getSize().y),
                                Particle.TELEPORT_PRE, spawnForewarn, true, SyncType.CREATESYNC);
                    }
                }
            }
            if (transitionTime <= 0.0f) {
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
     * @param fadeDelay: the delay in seconds before the screen fades out. If -1, indicates conditional respawn; not timed
     */
    public void beginTransition(PlayState state, TransitionState nextState, boolean override, float fadeSpeed, float fadeDelay) {
        if (override || this.nextState == null) {

            if (fadeDelay == -1) {
                this.nextState = null;
            } else {
                this.nextState = nextState;
                this.transitionTime = fadeDelay + 1.0f / fadeSpeed;
                this.spawnForewarned = false;
            }

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
        if (scores.getConnID() == 0) {

            //Create a new player
            short hitboxFilter = getHitBoxFilter().getFilter();
            state.setPlayer(state.createPlayer(startPoint, state.getGsm().getLoadout().getName(), player.getPlayerData().getLoadout(),
                    player.getPlayerData(),0, this, true, false, false, hitboxFilter));

            //focus camera on start point unless otherwise specified
            if (!player.isDontMoveCamera()) {
                state.getCamera().position.set(new Vector3(startPoint.getStartPos().x, startPoint.getStartPos().y, 0));
                state.getCameraFocusAimVector().setZero();
            }

            ((PlayerController) state.getController()).setPlayer(player);
        } else {
            if (player != null) {
                //alive check prevents duplicate players if entering/respawning simultaneously
                if (!player.isAlive()) {
                    String playerName = player.getName();
                    HadalGame.server.createNewClientPlayer(state, scores.getConnID(), playerName, player.getPlayerData().getLoadout(),
                            player.getPlayerData(), true, false, false, startPoint);
                }
            } else {
                //player is respawning from spectator and has no player
                HadalGame.server.createNewClientPlayer(state, scores.getConnID(), scores.getNameShort(), scoresExtra.getLoadout(),
                        null, true, false, false, startPoint);
            }
        }
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
        startOverridden = false;
    }

    public void setOverrideSpawn(Vector2 overrideSpawn) {
        overrideSpawnLocation.set(overrideSpawn);
        spawnOverridden = true;
    }

    public void setOverrideStart(Event event) {
        startPoint = event;
        startOverridden = true;
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

        if (teamFilter.getPalette().getIcon().getRGB().isZero()) {
            rgb.setZero();
        } else {
            rgb.set(teamFilter.getPalette().getIcon().getRGB());
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

    public UnlockEquip getLastEquippedPrimary() { return lastEquippedPrimary; }

    public void setLastEquippedPrimary(UnlockEquip lastEquippedPrimary) { this.lastEquippedPrimary = lastEquippedPrimary; }
}
