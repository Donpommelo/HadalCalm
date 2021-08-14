package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for pvp modes where players can see each other's Hp
 */
public class VisibleHp extends ModeModifier {

    private static final String settingTag = "visible_hp";
    private static final String uiText = "VISIBLE ENEMY HP?";
    private static final String name = "VISIBLE HP";

    public VisibleHp() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.setVisibleHp(true);
    }
}
