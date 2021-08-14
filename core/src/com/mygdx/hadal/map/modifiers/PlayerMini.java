package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.states.PlayState;

public class PlayerMini extends ModeModifier {

    private static final String settingTag = "player_mini";
    private static final String uiText = "SMALL PLAYERS?";
    private static final String name = "MINI";

    private static final float playerScale = 0.5f;
    private static final float zoomModifier = 0.5f;

    public PlayerMini() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.setPlayerDefaultScale(playerScale);
        state.setZoomModifier(state.getZoomModifier() * zoomModifier);
    }
}
