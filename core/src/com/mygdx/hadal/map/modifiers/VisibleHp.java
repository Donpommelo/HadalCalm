package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 *  @author Snotalini Swungo
 */
public class VisibleHp extends ModeModifier {

    private static final String settingTag = "visible_hp";
    private static final String uiText = "VISIBLE ENEMY HP?";
    private static final String name = "VISIBLE HP";

    public VisibleHp() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) {
        state.addMapModifier(UnlockArtifact.VISIBLE_HP);
    }
}
