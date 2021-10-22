package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.states.PlayState;

/**
 * This modifier makes the game world run at double-speed. This affects all bodies.
 * When active in conjuction with slo-mo modifier, the effects are canceled out
 * @author Pulgernon Phuyardee
 */
public class DoubleSpeed extends ModeModifier {

    private static final String settingTag = "double_speed";
    private static final String uiText = "DOUBLE SPEED?";
    private static final String name = "SPEED";

    private static final float physicsMultiplier = 2.0f;

    public DoubleSpeed() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) { state.setTimeModifier(state.getTimeModifier() * physicsMultiplier); }
}
