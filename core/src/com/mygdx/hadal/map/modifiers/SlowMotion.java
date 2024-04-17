package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * This modifier makes the game world run at half-speed. This affects all bodies.
 * When active in conjuction with fast forwards modifier, the effects are canceled out
 * @author Nemongrass Nurlando
 */
public class SlowMotion extends ModeModifier {

    private static final UIText uiText = UIText.MODIFIER_SLOW_UI;
    private static final UIText name = UIText.MODIFIER_SLOW;

    private static final float physicsMultiplier = -0.5f;

    public SlowMotion() {
        super(SettingSave.MODIFIER_SLOW, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.setTimeModifier(state.getTimeModifier() + physicsMultiplier); }
}
