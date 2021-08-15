package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.states.PlayState;

/**
 * @author Nemongrass Nurlando
 */
public class SlowMotion extends ModeModifier {

    private static final String settingTag = "slow_motion";
    private static final String uiText = "SLO-MO?";
    private static final String name = "SLOW";

    private static final float physicsMultiplier = 0.5f;

    public SlowMotion() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.setTimeModifier(state.getTimeModifier() * physicsMultiplier); }
}
