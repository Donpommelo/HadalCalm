package com.mygdx.hadal.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.hadal.HadalGame;

public class DesktopLauncher {

	private static final String TITLE = "Hadal Calm";

	public static void main (String[] arg) {

		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		//this makes the desktop icons appear
		config.setWindowIcon("128.png", "32.png","16.png");
		config.setTitle(TITLE);

		//this line prevents audio from cutting if too many sounds are playing
		config.setAudioConfig(192, 512, 9);
		config.setAutoIconify(true);

		config.setResizable(true);

		// These two lines were edited because without settings like this, Vsync gets applied twice, slowing things way down.
		config.useVsync(false);
		config.setForegroundFPS(60);
//
//		//this lets us decide whether to iconify or not dynamically in the settings menu
//		final boolean[] autoIconify = {true};
//		config.setWindowListener(new Lwjgl3WindowAdapter() {
//
//			@Override
//			public void iconified(boolean isIconified) {
//				if (!autoIconify[0] && isIconified) {
//					Gdx.app.postRunnable(() -> ((Lwjgl3Graphics) Gdx.graphics).getWindow().restoreWindow());
//				}
//			}
//		});

		new Lwjgl3Application(new HadalGame() {

			@Override
			public void setAutoIconify(boolean iconify) {
				//autoIconify[0] = iconify;
			}

		}, config);
	}
}
