package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 *  This modifier shrinks the bodies of all players.
 *  @author Gicciatello Grumpernickel
 */
public class PlayerMini extends ModeModifier {

    private static final String settingTag = "player_mini";
    private static final HText uiText = HText.MODIFIER_SMALL_UI;
    private static final HText name = HText.MODIFIER_SMALL;

    public PlayerMini() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) {
        state.addMapModifier(UnlockArtifact.PLAYER_MINI);
    }
}
