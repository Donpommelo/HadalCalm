package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.users.UserManager;

public class HadalGameHeadless extends HadalGame {

    @Override
    public void create() {
        usm = new UserManager();

        StateManager.states.push(new PlayStateHeadless(this, UnlockLevel.HUB_MULTI, GameMode.HUB, true, ""));
        StateManager.states.peek().show();

        server = new KryoServer(usm);
        server.init(true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        //update the state, update the ui, render the state, then render the ui.
        StateManager.update(delta);
    }

    @Override
    public void resize(int width, int height) {}
}
