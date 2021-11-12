package com.mygdx.hadal.map.modifiers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 *  This modifier makes all players and objects (including bullets and events) have 0 gravity
 *  @author Nathium Najandro
 */
public class ZeroGravity extends ModeModifier {

    private static final String settingTag = "zero_gravity";
    private static final HText uiText = HText.MODIFIER_ZERO_GRAV_UI;
    private static final HText name = HText.MODIFIER_ZERO_GRAV;

    public ZeroGravity() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) { state.getWorld().setGravity(new Vector2()); }
}
