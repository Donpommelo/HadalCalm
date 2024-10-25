package com.mygdx.hadal.managers;

import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

public class TimerManager {

    private final PlayState state;

    //Timer is used for timed scripted events. timerIncr is how much the timer should tick every update cycle (usually -1, 0 or 1)
    private float maxTimer, timer, timerIncr;

    //this is the displayed time
    private int currentTimer;
    private String displayedTimer;

    public TimerManager(PlayState state) {
        this.state = state;
    }

    /**
     * Change the game timer settings
     * @param timerSet: This sets the time to a designated amount
     * @param timerIncrement: This sets the amount of time that changes each second (usually -1, 0 or 1)
     */
    public void changeTimer(float timerSet, float timerIncrement) {
        maxTimer = timerSet;
        timer = timerSet;
        timerIncr = timerIncrement;

        state.getUIManager().getUiExtra().syncUIText(UITagType.TIMER);
    }

    //display a time warning when the time is low
    private final static float NOTIFICATION_THRESHOLD = 10.0f;
    /**
     * This increments the timer for timed levels. When time runs out, we want to run an event designated in the map (if it exists)
     * @param delta: amount of time that has passed since last update
     */
    public void incrementTimer(float delta) {

        if (timer > NOTIFICATION_THRESHOLD && timer + (timerIncr * delta) < NOTIFICATION_THRESHOLD) {
            state.getUIManager().getKillFeed().addNotification(UIText.TIMER_REMAINING.text(), false);
        }

        timer += (timerIncr * delta);

        //timer text changes after a whole second passes
        if ((int) timer != currentTimer) {
            currentTimer = (int) timer;

            //convert the time to minutes:seconds
            int seconds = currentTimer % 60;

            //this makes the timer have the same number of characters whether the seconds amount is 1 or 2 digits
            if (10 > seconds) {
                displayedTimer = currentTimer / 60 + ": 0" + seconds;
            } else {
                displayedTimer = currentTimer / 60 + ": " + seconds;
            }
            state.getUIManager().getUiExtra().syncUIText(UITagType.TIMER);
        }

        //upon timer running out, a designated event activates.
        if (0 >= timer && 0 > timerIncr) {
            if (null != state.getGlobalTimer()) {
                state.getGlobalTimer().getEventData().preActivate(null, null);
                timerIncr = 0;
            }
        }
    }

    public float getTimer() { return timer; }

    public void setTimer(float timer) { this.timer = timer; }

    public float getTimerIncr() { return timerIncr; }

    public void setTimerIncr(float timerIncr) { this.timerIncr = timerIncr; }

    public void setMaxTimer(float maxTimer) { this.maxTimer = maxTimer; }

    public float getMaxTimer() { return maxTimer; }

    public String getDisplayedTimer() { return displayedTimer; }

}
