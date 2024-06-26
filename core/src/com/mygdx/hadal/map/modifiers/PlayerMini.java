package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 *  This modifier shrinks the bodies of all players.
 *  @author Gicciatello Grumpernickel
 */
public class PlayerMini extends ModeModifier {

    private static final UIText uiText = UIText.MODIFIER_SMALL_UI;
    private static final UIText name = UIText.MODIFIER_SMALL;

    public PlayerMini() {
        super(SettingSave.MODIFIER_SMALL, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.addMapModifier(UnlockArtifact.PLAYER_MINI);
    }
}
