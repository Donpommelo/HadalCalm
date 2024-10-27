package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.HadalGame;

import static com.mygdx.hadal.HadalGame.CONFIG_HEIGHT;
import static com.mygdx.hadal.HadalGame.CONFIG_WIDTH;

public class FadeManager {

    //this is the rate at which the screen fades from/to black.
    private static final float DEFAULT_FADE_IN_SPEED = -2.0f;
    private static final float DEFAULT_FADE_OUT_SPEED = 2.0f;

    //This is the how faded the black screen is. (starts off black)
    private static float fadeLevel = 1.0f;

    //Amount of delay before fade transition occurs
    private static float fadeDelay = 0.0f;

    //if set to true, we jump right into transition without fading in/out (set by making fadeDelta 0 in fadeSpecificSpeed())
    private static boolean skipFade;

    //This is how much the fade changes every engine tick (starts out fading in)
    private static float fadeDelta = DEFAULT_FADE_IN_SPEED;

    //this is a runnable that will run when the game finishes a transition, usually to another state.
    private static Runnable runAfterTransition;

    //this is a black texture used for fading in/out transitions.
    private static final Texture black = new Texture(Gdx.files.internal("black.png"));

    public static void render(HadalGame app, SpriteBatch batch) {

        //Render the black image used for fade transitions
        if (0.0f < fadeLevel) {
            batch.setProjectionMatrix(app.getHud().combined);
            batch.begin();
            batch.setColor(1.0f, 1.0f, 1.0f, fadeLevel);
            batch.draw(black, 0.0f, 0.0f, CONFIG_WIDTH, CONFIG_HEIGHT);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            batch.end();
        }
    }

    public static void controller(float delta) {

        //only fade when the states specifies that transitions should fade (i.e. no fade when closing pause menu)
        if (StateManager.states.peek().processTransitions()) {

            //If we are in the delay period of a transition, decrement the delay
            if (0.0f < fadeDelay) {
                fadeDelay -= delta;
            } else if (skipFade) {

                //for special transitions, we skip the fade and transition immediately after delay (play -> results)
                //important to set runAfterTransition to null afterwards to avoid potential double transitions
                skipFade = false;
                if (null != runAfterTransition) {
                    Gdx.app.postRunnable(runAfterTransition);
                    runAfterTransition = null;
                }
            } else if (0.0f > fadeDelta) {

                //If we are fading in and not done yet, decrease fade.
                fadeLevel += fadeDelta * delta;

                //If we just finished fading in, set fade to 0
                if (0.0f > fadeLevel) {
                    fadeLevel = 0.0f;
                    fadeDelta = 0.0f;
                }
            } else if (0.0f < fadeDelta) {

                //If we are fading out and not done yet, increase fade.
                fadeLevel += fadeDelta * delta;

                //If we just finished fading out, set fade to 1 and do a transition
                if (1.0f <= fadeLevel) {
                    fadeLevel = 1.0f;
                    fadeDelta = 0.0f;
                    if (null != runAfterTransition) {
                        Gdx.app.postRunnable(runAfterTransition);
                        runAfterTransition = null;
                    }
                }
            }
        }
    }

    public static void dispose() { black.dispose(); }

    /**
     * This makes the game fade at a specific speed. Can be positive or negative to fade out or in
     * @param fadeDelay: How much delay until the fading begins?
     * @param fadeSpeed: speed of the fading. IF sest to 0, we skip thte fade entirely
     */
    public static void fadeSpecificSpeed(float fadeSpeed, float fadeDelay) {
        FadeManager.fadeDelta = fadeSpeed;
        FadeManager.fadeDelay = fadeDelay;
        if (0.0f == fadeDelta) {
            skipFade = true;
        }
    }

    public static void fadeOut() {	fadeDelta = DEFAULT_FADE_OUT_SPEED; }

    public static void fadeIn() { fadeDelta = DEFAULT_FADE_IN_SPEED; }

    public static float getFadeLevel() { return FadeManager.fadeLevel; }

    public static void setFadeLevel(float fadeLevel) { FadeManager.fadeLevel = fadeLevel; }

    public static void setRunAfterTransition(Runnable runAfterTransition) { FadeManager.runAfterTransition = runAfterTransition; }
}
