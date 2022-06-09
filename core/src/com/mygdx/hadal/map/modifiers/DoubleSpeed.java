package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * This modifier makes the game world run at double-speed. This affects all bodies.
 * When active in conjuction with slo-mo modifier, the effects are canceled out
 * @author Pulgernon Phuyardee
 */
public class DoubleSpeed extends ModeModifier {

    private static final String settingTag = "double_speed";
    private static final UIText uiText = UIText.MODIFIER_FAST_UI;
    private static final UIText name = UIText.MODIFIER_FAST;

    private static final float physicsMultiplier = 1.0f;

    public DoubleSpeed() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.setTimeModifier(state.getTimeModifier() + physicsMultiplier); }
}
