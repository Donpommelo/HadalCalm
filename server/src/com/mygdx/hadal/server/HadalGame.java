package com.mygdx.hadal.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.server.managers.LobbyManager;
import com.mygdx.hadal.server.managers.ServerManager;

import java.io.IOException;

public class HadalGame extends ApplicationAdapter {

    //this is the game's version. This must match between client and host to connect.
    public static final String VERSION = "1.0.9L";

    @Override
    public void create() {

        ServerManager.init();
        LobbyManager.init();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        LobbyManager.controller(delta);
    }

    @Override
    public void dispose() {
        try {
            ServerManager.dispose();
            LobbyManager.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
