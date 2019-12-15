package com.mygdx.hadal.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.hadal.HadalGame;

public class DesktopLauncher {
	
	private static final String TITLE = "Hadal Panic";
	private final static int DEFAULT_WIDTH = 1080;
	private final static int DEFAULT_HEIGHT = 720;
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = TITLE;
		config.width = DEFAULT_WIDTH;
		config.height = DEFAULT_HEIGHT;
		config.fullscreen = true;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		new LwjglApplication(new HadalGame(), config);
	}
}
