package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 * This mode modifier causes all players to have 0 traction with the ground
 * @author Yobaganoush Yogger
 */
public class PlayerSlide extends ModeModifier {

    private static final String settingTag = "player_slide";
    private static final HText uiText = HText.MODIFIER_SLIDE_UI;
    private static final HText name = HText.MODIFIER_SLIDE;

    public PlayerSlide() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.addMapModifier(UnlockArtifact.PLAYER_SLIDE);
    }
}
