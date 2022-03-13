package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 *  This modifier increases the sizes of all players.
 *  @author Linjsalt Lonkkoff
 */
public class PlayerGiant extends ModeModifier {

    private static final String settingTag = "player_giant";
    private static final HText uiText = HText.MODIFIER_LARGE_UI;
    private static final HText name = HText.MODIFIER_LARGE;

    public PlayerGiant() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.addMapModifier(UnlockArtifact.PLAYER_GIANT); }

}
