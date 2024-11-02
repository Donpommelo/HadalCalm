package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Collections;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.managers.loaders.*;
import com.mygdx.hadal.server.states.PlayStateHeadless;
import com.mygdx.hadal.users.UserManager;

import java.io.IOException;

public class HadalGameHeadless extends HadalGame {

    @Override
    public void create() {
        EffectEntityManager.initLoader(new EffectEntityLoaderHeadless());
        SpriteManager.initLoader(new SpriteLoaderHeadless());
        ShaderManager.initLoader(new ShaderLoaderHeadless());
        SoundManager.initLoader(new SoundLoaderHeadless());
        RagdollManager.initLoader(new RagdollLoaderHeadless());

        usm = new UserManager();

        JSONManager.initJSON();

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

    @Override
    public void dispose() {
        StateManager.dispose();
        BotManager.terminatePathfindingThreads();
        try {
            if (server != null) {
                server.dispose();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
