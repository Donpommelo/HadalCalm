package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.UIObjective;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.states.PlayState;

public class UIObjectiveHeadless extends UIObjective {

    public UIObjectiveHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void addObjective(HadalEntity objective, Sprite sprite, HadalColor color, boolean displayObjectiveOffScreen,
                             boolean displayObjectiveOnScreen, boolean displayClearCircle) {

    }
}
