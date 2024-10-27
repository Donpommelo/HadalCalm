package com.mygdx.hadal.server.managers;

import com.mygdx.hadal.managers.UIManager;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.server.actors.*;
import com.mygdx.hadal.states.PlayState;

public class UIManagerHeadless extends UIManager {

    public UIManagerHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void initUIElementsCreate() {
        this.uiArtifact = new UIArtifactsHeadless(state);
        this.uiExtra = new UIExtraHeadless(state);
        this.uiObjective = new UIObjectiveHeadless(state);

        this.chatWheel = new ChatWheelHeadless(state);

        this.killFeed = new KillFeedHeadless(state);
        this.scoreWindow = new ScoreWindowHeadless(state);
    }

    @Override
    public void setBoss(Enemy enemy) {}

    @Override
    public void clearBoss() {}
}
