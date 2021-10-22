package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 *  This modifier allows all players to see each other's hp bars, for enemies and allies alike
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
