package com.mygdx.hadal.server.states;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.SpawnManager;
import com.mygdx.hadal.managers.SpectatorManager;
import com.mygdx.hadal.managers.TimerManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.managers.CameraManagerHeadless;
import com.mygdx.hadal.server.managers.EndgameManagerHeadless;
import com.mygdx.hadal.server.managers.TransitionManagerHeadless;
import com.mygdx.hadal.server.managers.UIManagerHeadless;
import com.mygdx.hadal.states.PlayState;

public class PlayStateHeadless extends PlayState {

    public PlayStateHeadless(HadalGame app, UnlockLevel level, GameMode mode, boolean reset, String startID) {
        super(app, level, mode, true, reset, startID);
    }

    public void initManagers(String startID) {
        this.cameraManager = new CameraManagerHeadless(this, map);
        this.uiManager = new UIManagerHeadless(this);

        this.timerManager = new TimerManager(this);
        this.spawnManager = new SpawnManager(this, startID);
        this.transitionManager = new TransitionManagerHeadless(this);
        this.spectatorManager = new SpectatorManager(this);
        this.endgameManager = new EndgameManagerHeadless(this);
    }

    @Override
    public void show() {}
}
