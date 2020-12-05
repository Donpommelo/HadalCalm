package com.mygdx.hadal.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.hadal.HadalGame;

public class DesktopLauncher {

	private static final String TITLE = "Hadal Calm";
	public static final int CONFIG_WIDTH = 1280;
	public static final int CONFIG_HEIGHT = 720;

	public static void main (String[] arg) {
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		//this makes the desktop icons appear
		config.setWindowIcon("128.png", "32.png","16.png");
		config.setTitle(TITLE);

		//this line prevents audio from cutting if too many sounds are playing
		config.setAudioConfig(192, 512, 9);
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setAutoIconify(true);

		new Lwjgl3Application(new HadalGame() {
			
			@Override
			public void setFrameRate(int framerate) {
				
				//This exposes config to the app to change fps during runtime.
				config.setForegroundFPS(framerate);
			}

			@Override
			public void setAutoIconify(boolean iconify) {
				config.setAutoIconify(iconify);
			}
			
		}, config);
	}
}
