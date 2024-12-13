package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

public class ServerLauncher {

    public static void main(String[] args) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        config.updatesPerSecond = 30;

        new HeadlessApplication(new HadalGameHeadless(), config);

        // Mock the OpenGL context to prevent errors
        Gdx.gl = new MockGL2();
        Gdx.gl20 = Gdx.gl;
    }
}