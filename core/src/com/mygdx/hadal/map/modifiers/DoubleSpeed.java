package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.states.PlayState;

public class DoubleSpeed extends ModeModifier {

    private static final String settingTag = "double_speed";
    private static final String uiText = "DOUBLE SPEED?";
    private static final String name = "SPEED";

    private static final float physicsMultiplier = 2.0f;

    public DoubleSpeed() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.setTimeModifier(state.getTimeModifier() * physicsMultiplier); }
}
