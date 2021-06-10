package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where players do not inflict damage to one another.
 * @author Quizmeister Quifield
 */
public class ToggleNoDamage extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setNoDamage(true);
    }
}
