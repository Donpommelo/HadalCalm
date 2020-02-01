package com.mygdx.hadal.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.hadal.HadalGame;

public class DesktopLauncher {
	
	private static final String TITLE = "Hadal Panic";
	
	public static void main (String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = TITLE;
		config.resizable = false;
		config.foregroundFPS = 60;
		new LwjglApplication(new HadalGame() {
			
			@Override
			public void setFrameRate(int framerate) {
				config.foregroundFPS = framerate;
			};
			
			
		}, config);
	}
}
