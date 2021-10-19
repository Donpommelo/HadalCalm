package com.mygdx.hadal.map.modifiers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.states.PlayState;

public class ZeroGravity extends ModeModifier {

    private static final String settingTag = "zero_gravity";
    private static final String uiText = "ZERO GRAVITY?";
    private static final String name = "FLOAT";

    public ZeroGravity() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) { state.getWorld().setGravity(new Vector2()); }
}
