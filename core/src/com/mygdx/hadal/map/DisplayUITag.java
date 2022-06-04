package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting sets the ui tags that will be visible in the upper left hand corner
 * @author Blinnifer Butmeg
 */
public class DisplayUITag extends ModeSetting {

    private final String uiTag;

    public DisplayUITag(String uiTag) {
        this.uiTag = uiTag;
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) { return uiTag; }
}
