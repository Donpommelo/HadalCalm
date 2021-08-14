package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

public class PlayerBounce extends ModeModifier {

    private static final String settingTag = "player_bounce";
    private static final String uiText = "BOUNCY PLAYERS?";
    private static final String name = "BOUNCE";

    public PlayerBounce() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.addMapModifier(UnlockArtifact.PLAYER_BOUNCE); }
}
