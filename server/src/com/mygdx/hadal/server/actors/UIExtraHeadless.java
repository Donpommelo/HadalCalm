package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.UIExtra;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.states.PlayState;

public class UIExtraHeadless extends UIExtra {

    public UIExtraHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void syncUIText(UITagType changedType) {}

    @Override
    public void changeTypes(String tags, boolean clear) {}
}
