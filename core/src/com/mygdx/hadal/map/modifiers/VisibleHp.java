package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 *  This modifier allows all players to see each other's hp bars, for enemies and allies alike
 *  @author Snotalini Swungo
 */
public class VisibleHp extends ModeModifier {

    private static final UIText uiText = UIText.MODIFIER_VISIBLE_HP_UI;
    private static final UIText name = UIText.MODIFIER_VISIBLE_HP;

    public VisibleHp() {
        super(SettingSave.MODIFIER_VISIBLE_HP, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.addMapModifier(UnlockArtifact.VISIBLE_HP);
    }
}
