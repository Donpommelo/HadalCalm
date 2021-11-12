package com.mygdx.hadal.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.mygdx.hadal.HadalGame;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * The DesktopLauncher launches the game
 * It is also responsible for starting configuration stuff like window icon, title
 */
public class DesktopLauncher {

	private static final String TITLE = "Hadal Calm";

	public static void main (String[] arg) throws FileNotFoundException {

		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		//this makes the desktop icons appear
		config.setWindowIcon("128.png", "32.png", "16.png");
		config.setTitle(TITLE);

		//this line prevents audio from cutting if too many sounds are playing
		//I also increased bufferCount to hopefully prevent "unable to allocate audio buffers" error
		config.setAudioConfig(192, 512, 11);
		config.setResizable(true);

		// These two lines were edited because without settings like this, Vsync gets applied twice, slowing things way down.
		config.useVsync(false);
		config.setForegroundFPS(60);

		//this lets us decide whether to iconify or not dynamically in the settings menu
		final boolean[] autoIconify = {true};
		config.setWindowListener(new Lwjgl3WindowAdapter() {

			@Override
			public void iconified(boolean isIconified) {
				if (!autoIconify[0] && isIconified) {
					Gdx.app.postRunnable(() -> ((Lwjgl3Graphics) Gdx.graphics).getWindow().restoreWindow());
				}
			}
		});

		//This causes crashes to log in a text file
		System.setErr(new PrintStream("err.txt"));

		//create app. We override the iconify method to allow that setting to be changed
		new Lwjgl3Application(new HadalGame() {

			@Override
			public void setAutoIconify(boolean iconify) {
				autoIconify[0] = iconify;
			}
		}, config);
	}
}
