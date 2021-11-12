package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 *  This modifier allows all players to see each other's hp bars, for enemies and allies alike
 *  @author Snotalini Swungo
 */
public class VisibleHp extends ModeModifier {

    private static final String settingTag = "visible_hp";
    private static final HText uiText = HText.MODIFIER_VISIBLE_HP_UI;
    private static final HText name = HText.MODIFIER_VISIBLE_HP;

    public VisibleHp() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) {
        state.addMapModifier(UnlockArtifact.VISIBLE_HP);
    }
}
