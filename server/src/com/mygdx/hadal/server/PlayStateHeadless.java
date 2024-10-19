package com.mygdx.hadal.server;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.PlayState;

public class PlayStateHeadless extends PlayState {

    public PlayStateHeadless(HadalGame app, UnlockLevel level, GameMode mode, boolean reset, String startID) {
        super(app, level, mode, true, reset, startID);
    }

    public void initManagers(String startID) {
//        this.renderManager = new RenderManager(this, map);
        this.cameraManager = new CameraManager(this, map);
//        this.uiManager = new UIManager(this);
        this.timerManager = new TimerManager(this);
        this.spawnManager = new SpawnManager(this, startID);
        this.transitionManager = new TransitionManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.endgameManager = new EndgameManager(this);
    }

    @Override
    public void update(float delta) {

    }
}
