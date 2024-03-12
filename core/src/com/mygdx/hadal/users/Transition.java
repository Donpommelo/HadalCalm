package com.mygdx.hadal.users;

import com.mygdx.hadal.states.PlayState.TransitionState;

/**
 * A Transition represents a single instance of a player transitioning to another state.
 * This includes spawning, new level, results screen, spectator etc.
 * Spawning (either respawning or spawning in a new level) is the most common and this contains several fields to keep track
 * of things like respawn time, forewarn timings and other properties of the respawn
 */
public class Transition {

    public static final float DEFAULT_FADE_OUT_SPEED = 2.0f;
    public static final float DEFAULT_FADE_DELAY = 0.0f;
    public static final float SHORT_FADE_DELAY = 0.5f;
    public static final float MEDIUM_FADE_DELAY = 1.0f;
    public static final float LONG_FADE_DELAY = 1.5f;

    private static final float SPAWN_FOREWARN = 2.0f;

    //This describes the type of transition
    private TransitionState nextState = TransitionState.RESPAWN;

    //Number of seconds before the screen begins to fade
    private float fadeDelay = DEFAULT_FADE_DELAY;

    //Speed of the fade out once it begins
    private float fadeSpeed = DEFAULT_FADE_OUT_SPEED;

    //Number of seconds before spawn particles appear in the respawn location (if >fadeDelay, no particles)
    private float forewarnTime = SPAWN_FOREWARN;

    //Do spawn particles appear at all? Does this override another transition? Should the camera center on the player once spawned in?
    private boolean spawnForewarned, override, centerCameraOnStart, skipFade;

    //Should this transition reset player data? (If respawning)
    private boolean reset = true;

    public TransitionState getNextState() { return nextState; }

    public float getFadeSpeed() { return fadeSpeed; }

    public float getFadeDelay() { return fadeDelay; }

    public float getForewarnTime() { return forewarnTime; }

    public boolean isSpawnForewarned() { return spawnForewarned; }

    public boolean isOverride() { return override; }

    public boolean isCenterCameraOnStart() { return centerCameraOnStart; }

    public boolean isSkipFade() { return skipFade; }

    public boolean isReset() { return reset; }

    public Transition setNextState(TransitionState nextState) {
        this.nextState = nextState;
        return this;
    }

    public Transition setFadeSpeed(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;
        return this;
    }

    public Transition setFadeDelay(float fadeDelay) {
        this.fadeDelay = fadeDelay;
        return this;
    }

    public Transition setForewarnTime(float forewarnTime) {
        this.forewarnTime = forewarnTime;
        return this;
    }

    public Transition setSpawnForewarned(boolean spawnForewarned) {
        this.spawnForewarned = spawnForewarned;
        return this;
    }

    public Transition setOverride(boolean override) {
        this.override = override;
        return this;
    }

    public Transition setCenterCameraOnStart(boolean centerCameraOnStart) {
        this.centerCameraOnStart = centerCameraOnStart;
        return this;
    }

    public Transition setSkipFade(boolean skipFade) {
        this.skipFade = skipFade;
        return this;
    }

    public Transition setReset(boolean reset) {
        this.reset = reset;
        return this;
    }
}
