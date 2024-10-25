package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.ScoreWindow;
import com.mygdx.hadal.states.PlayState;

public class ScoreWindowHeadless extends ScoreWindow {

    public ScoreWindowHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void syncScoreTable() {}

    @Override
    public void syncSettingTable() {}
}
