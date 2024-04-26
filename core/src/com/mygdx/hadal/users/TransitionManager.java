package com.mygdx.hadal.users;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

import static com.mygdx.hadal.users.Transition.LONG_FADE_DELAY;

/**
 * This manages a user's transitions, most notably respawns.
 */
public class TransitionManager {

    //The user whose transitions this managers
    private final User user;

    //the state the user is currently transitioning to. (If null, the user is not currently transitioning)
    private TransitionState nextState;

    //These keep track of the number seconds before transitioning and creating respawn particles
    private float transitionTime, forewarnTime;

    //Will respawn particles be created? Have respawn particles already been created?
    private boolean spawnForewarned, particlesSpawned;

    //the start point this user will respawn at next. Used to draw particles at the point prior to respawning
    private Event startPoint;

    //used when the player is spawned at a set location instead of using a start point (for matryoshka mode instant repawn)
    private final Vector2 overrideSpawnLocation = new Vector2();
    private boolean spawnOverridden, startOverridden;

    //does this respawn reset the player's hp/fuel etc (false for single-player campaign level transitions)
    private boolean reset;

    public TransitionManager(User user) {
        this.user = user;
    }

    public void controller(PlayState state, float delta) {

        //we keep track of each user's transition duration, so that we can make them respawn at the correct time
        if (nextState != null) {
            transitionTime -= delta;

            //briefly before respawning, we want to flash particles at prospective spawn location
            if (transitionTime <= forewarnTime && !particlesSpawned) {
                if (TransitionState.RESPAWN.equals(nextState)) {
                    particlesSpawned = true;

                    if (spawnForewarned) {

                        //this avoids forewarn particles persisting after respawn if respawn is too fast
                        if (forewarnTime > transitionTime && transitionTime > 0.0f) {
                            forewarnTime = transitionTime;
                        }

                        if (user.getEffectManager().isShowSpawnParticles()) {
                            if (spawnOverridden) {
                                new ParticleEntity(state, new Vector2(overrideSpawnLocation).sub(0, startPoint.getSize().y),
                                        Particle.TELEPORT_PRE, forewarnTime, true, SyncType.CREATESYNC);
                            } else {
                                new ParticleEntity(state, new Vector2(startPoint.getStartPos()).sub(0, startPoint.getSize().y),
                                        Particle.TELEPORT_PRE, forewarnTime, true, SyncType.CREATESYNC);
                            }
                        }
                    }
                }
            }
            if (transitionTime <= 0.0f) {
                if (TransitionState.RESPAWN.equals(nextState)) {
                    respawn(state);
                }
                nextState = null;
            }
        }
    }

    /**
     * This is run when a user transitions to another state.
     */
    public void beginTransition(PlayState state, Transition transition) {
        //If we are in the middle of another transition, we skip this unless this transition is set to "override"
        if (transition.isOverride() || this.nextState == null) {
            spawnForewarned = transition.isSpawnForewarned();
            reset = transition.isReset();

            //-1 delay indicates conditional respawn; not timed. The user won't change until something occurs to respawn them
            if (transition.getFadeDelay() == -1) {
                this.nextState = null;
            } else {
                this.nextState = transition.getNextState();
                this.transitionTime = transition.getFadeDelay();
                if (0.0f != transition.getFadeSpeed()) {
                    this.transitionTime += 1.0f / transition.getFadeSpeed();
                }
                this.forewarnTime = transition.getForewarnTime();
                this.particlesSpawned = false;
            }

            Vector2 clientStartPosition = null;

            //set respawn point upon respawn initializing so we know where it is when we draw spawn particles
            if (TransitionState.RESPAWN.equals(nextState)) {
                if (!startOverridden) {
                    startPoint = state.getSavePoint(user);

                    //if desired, set camera to prospective respawn point right away (for initial spawn)
                    if (transition.isCenterCameraOnStart()) {
                        if (user.getConnID() == 0) {
                            state.getCameraManager().setCameraPosition(startPoint.getStartPos());
                            state.getCameraManager().setCameraTarget(startPoint.getStartPos());
                            state.getCameraManager().getCameraFocusAimVector().setZero();
                        } else {
                            clientStartPosition = startPoint.getStartPos();
                        }
                    }
                }
            }

            if (user.getConnID() == 0) {
                //this extra check is for state transitions, not user transitions
                if (transition.isOverride() || state.getNextState() == null) {
                    state.beginTransition(nextState, transition.getFadeSpeed(), transition.getFadeDelay(), transition.isSkipFade());
                }
            } else if (user.getConnID() > 0) {
                HadalGame.server.sendToTCP(user.getConnID(), new Packets.ClientStartTransition(nextState,
                        transition.getFadeSpeed(), transition.getFadeDelay(), transition.isSkipFade(), clientStartPosition));
            }
        }
    }

    /**
     * This respawns a player into the world. Run only by host
     */
    public void respawn(PlayState state) {
        PlayerBodyData playerData = null;
        if (null != user.getPlayer() && !reset) {
            playerData = user.getPlayer().getPlayerData();
        }

        if (user.getConnID() == 0) {

            //Create a new player for the host using their existing player data (if existent) and filter
            short hitboxFilter = user.getHitboxFilter().getFilter();

            //create player and set it as our own
            HadalGame.usm.setOwnPlayer(state.createPlayer(startPoint, JSONManager.loadout.getName(), new Loadout(JSONManager.loadout),
                    playerData, user, reset, false, hitboxFilter));

            //focus camera on start point unless otherwise specified
            if (!user.getPlayer().isDontMoveCamera()) {
                state.getCameraManager().setCameraPosition(startPoint.getStartPos());
                state.getCameraManager().getCameraFocusAimVector().setZero();
                state.getCameraManager().setCameraTarget(null);
            }

            //hook up current controller to new player
            ((PlayerController) state.getController()).setPlayer(user.getPlayer());
        } else {

            //alive check prevents duplicate players if entering/respawning simultaneously
            if (null == user.getPlayer() || (null != user.getPlayer() && !user.getPlayer().isAlive())) {
                user.getLoadoutManager().setActiveLoadout(user.getLoadoutManager().getSavedLoadout());
                HadalGame.server.createNewClientPlayer(state, user, playerData, reset, startPoint);
            }
        }
    }

    /**
     * This is called to make a new user respawn a player.
     * This begins a transition that will spawn the player into a new playstate
     * Different fields are used depending on whether we reset the player's hp/fuel/etc (stage transition) vs new level
     */
    public void levelStartSpawn(PlayState state, boolean reset) {
        if (!user.isSpectator()) {
            nextState = null;
            if (reset) {
                beginTransition(state,
                        new Transition()
                                .setNextState(PlayState.TransitionState.RESPAWN)
                                .setFadeDelay(LONG_FADE_DELAY)
                                .setFadeSpeed(0.0f)
                                .setForewarnTime(LONG_FADE_DELAY)
                                .setSpawnForewarned(true)
                                .setCenterCameraOnStart(true)
                                .setSkipFade(true));
            } else {
                beginTransition(state,
                        new Transition()
                                .setNextState(PlayState.TransitionState.RESPAWN)
                                .setFadeSpeed(0.0f)
                                .setReset(false));
            }
        }
    }

    /**
     * Need to reset all these fields in case level change occurs mid-respawn for a player
     */
    public void newLevelReset() {
        spawnOverridden = false;
        startOverridden = false;
        startPoint = null;
        nextState = null;
    }

    public void setOverrideSpawn(Vector2 overrideSpawn) {
        overrideSpawnLocation.set(overrideSpawn);
        spawnOverridden = true;
    }

    public Vector2 getOverrideSpawnLocation() { return overrideSpawnLocation; }

    public boolean isSpawnOverridden() { return spawnOverridden; }

    public void setOverrideStart(Event event) {
        startPoint = event;
        startOverridden = true;
    }
}
