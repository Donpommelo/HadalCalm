package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * This mode modifier causes all players to have 0 traction with the ground
 * @author Yobaganoush Yogger
 */
public class PlayerSlide extends ModeModifier {

    private static final UIText uiText = UIText.MODIFIER_SLIDE_UI;
    private static final UIText name = UIText.MODIFIER_SLIDE;

    public PlayerSlide() {
        super(SettingSave.MODIFIER_SLIPPERY, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.addMapModifier(UnlockArtifact.PLAYER_SLIDE);
    }
}
