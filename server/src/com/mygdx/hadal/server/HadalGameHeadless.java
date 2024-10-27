package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Collections;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.FadeManager;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.users.UserManager;

public class HadalGameHeadless extends HadalGame {

    @Override
    public void create() {
        usm = new UserManager();

        JSONManager.initJSON(this);

        StateManager.states.push(new PlayStateHeadless(this, UnlockLevel.HUB_MULTI, GameMode.HUB, true, ""));
        StateManager.states.peek().show();

        server = new KryoServer(usm);
        server.init(true, true);

        //this is necessary to prevent nested iterations from causing errors
        Collections.allocateIterators = true;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        //update the state, update the ui, render the state, then render the ui.
        StateManager.update(delta);

        //FadeManager still used for delays, but not for fading
        FadeManager.controller(delta);
    }

    @Override
    public void resize(int width, int height) {}
}
