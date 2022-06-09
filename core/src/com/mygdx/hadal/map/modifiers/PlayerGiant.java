package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 *  This modifier increases the sizes of all players.
 *  @author Linjsalt Lonkkoff
 */
public class PlayerGiant extends ModeModifier {

    private static final String settingTag = "player_giant";
    private static final UIText uiText = UIText.MODIFIER_LARGE_UI;
    private static final UIText name = UIText.MODIFIER_LARGE;

    public PlayerGiant() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.addMapModifier(UnlockArtifact.PLAYER_GIANT); }

}
