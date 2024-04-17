package com.mygdx.hadal.map.modifiers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 *  This modifier makes all players and objects (including bullets and events) have 0 gravity
 *  @author Nathium Najandro
 */
public class ZeroGravity extends ModeModifier {

    private static final UIText uiText = UIText.MODIFIER_ZERO_GRAV_UI;
    private static final UIText name = UIText.MODIFIER_ZERO_GRAV;

    public ZeroGravity() {
        super(SettingSave.MODIFIER_ZERO_GRAVITY, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.getWorld().setGravity(new Vector2()); }
}
