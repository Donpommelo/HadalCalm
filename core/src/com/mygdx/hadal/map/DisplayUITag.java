package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class DisplayUITag extends ModeSetting {

    private final String uiTag;

    public DisplayUITag(String uiTag) {
        this.uiTag = uiTag;
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        return uiTag;
    }
}
