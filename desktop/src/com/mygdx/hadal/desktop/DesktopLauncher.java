package com.mygdx.hadal.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.hadal.HadalGame;

public class DesktopLauncher {
	
	private static final String TITLE = "Hadal Calm";
	
	public static void main (String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = TITLE;
		config.resizable = false;
		config.audioDeviceSimultaneousSources = 128;
		new LwjglApplication(new HadalGame() {
			
			@Override
			public void setFrameRate(int framerate) {
				
				//This exposes config to the app to change fps during runtime.
				config.foregroundFPS = framerate;
			};
			
		}, config);
	}
}
