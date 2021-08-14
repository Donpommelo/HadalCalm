package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

public class PlayerInvisible extends ModeModifier {

    private static final String settingTag = "player_invisible";
    private static final String uiText = "INVISIBLE PLAYERS?";
    private static final String name = "INVISIBILITY";

    public PlayerInvisible() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.addMapModifier(UnlockArtifact.PLAYER_INVISIBLE); }
}
