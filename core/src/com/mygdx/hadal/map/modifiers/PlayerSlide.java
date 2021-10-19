package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Yobaganoush Yogger
 */
public class PlayerSlide extends ModeModifier {

    private static final String settingTag = "player_slide";
    private static final String uiText = "SLIPPERY PLAYERS?";
    private static final String name = "SLIDE";

    public PlayerSlide() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) {
        state.addMapModifier(UnlockArtifact.PLAYER_SLIDE);
    }
}
