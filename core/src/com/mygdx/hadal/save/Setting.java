package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingLives;
import com.mygdx.hadal.map.SettingTimer;
import com.mygdx.hadal.states.PlayState;

import java.util.HashMap;
import java.util.Map;

/**
 * A Setting contains all saved game settings.
 * @author Vlurgundy Vluginald
 */
public class Setting {

	private int resolution, framerate, cursorType, cursorSize, cursorColor, maxPlayers, artifactSlots,
		portNumber, hitsoundType, customShader;
	private boolean fullscreen, autoIconify, vsync, debugHitbox, displayNames, displayHp, randomNameAlliteration,
		consoleEnabled, verboseDeathMessage, multiplayerPause, exportChatLog, enableUPNP, hideHUD, mouseCameraTrack;
	private float soundVolume, musicVolume, masterVolume, hitsoundVolume;

	//connecting clients need to know this password to enter the server
	private String serverPassword;

	private Map<String, Map<String, Integer>> modeSettings;

	//this is the last cursor used. We save this so we can dispose of it properly
	private static Cursor lastCursor;

	public Setting() {}

	/**
	 * This simply saves the settings in a designated file
	 */
	public void saveSetting() {
		Gdx.files.local("save/Settings.json").writeString(GameStateManager.json.prettyPrint(this), false);
	}

	/**
	 * This sets display settings, changing screen size/vsync/framerate
	 */
	public void setDisplay(HadalGame game, PlayState state) {
		Monitor currMonitor = Gdx.graphics.getMonitor();
    	DisplayMode displayMode = Gdx.graphics.getDisplayMode(currMonitor);

    	//set fullscreen + vsync. No vsync is used if not in fullscreen
    	if (fullscreen) {
    		Gdx.graphics.setFullscreenMode(displayMode);
			Gdx.graphics.setVSync(vsync);
		} else {
    		indexToResolution();
			Gdx.graphics.setVSync(false);
		}

		Gdx.graphics.setForegroundFPS(indexToFramerate());

    	game.setAutoIconify(autoIconify);

    	if (state != null) {
    		state.toggleVisibleHitboxes(debugHitbox);
    	}

    	//resizing here (possibly) deals with some fullscreen camera issues on certain devices?
    	game.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/**
	 * This sets the player's cursor according to their saved settings
	 * cursorType == 0: default cursor
	 * cursorType == 1: crosshair cursor
	 * cursorType == 2: dot cursor
	 */
	private static final int pixmapSize = 128;
	public void setCursor() {

		//when we set a new cursor, we dispose of the old one (if existent)
		if (lastCursor != null) {
			lastCursor.dispose();
		}

		if (cursorType == 0) {
			Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
			lastCursor = null;
		} else {

			Pixmap cursor = new Pixmap(Gdx.files.internal(indexToCursorType()));

			Pixmap pm = new Pixmap(pixmapSize, pixmapSize, Pixmap.Format.RGBA8888);

			int scaledWidth = (int) (indexToCursorScale() * cursor.getWidth());
			int scaledHeight = (int) (indexToCursorScale() * cursor.getHeight());

			pm.drawPixmap(cursor,
				0, 0, cursor.getWidth() + 1, cursor.getHeight() + 1,
				(pixmapSize - scaledWidth) / 2, (pixmapSize - scaledHeight) / 2, scaledWidth, scaledHeight);

			Color newColor = indexToCursorColor();
			for (int y = 0; y < pm.getHeight(); y++) {
				for (int x = 0; x < pm.getWidth(); x++) {
					Color color = new Color();
					Color.rgba8888ToColor(color, pm.getPixel(x, y));
					if (color.a != 0.0f) {
						pm.setColor(newColor.r, newColor.g, newColor.b, color.a);
						pm.fillRectangle(x, y, 1, 1);
					}
				}
			}

			Cursor newCursor = Gdx.graphics.newCursor(pm, pixmapSize / 2, pixmapSize / 2);
	    	Gdx.graphics.setCursor(newCursor);
			lastCursor = newCursor;
	    	pm.dispose();
	    	cursor.dispose();
		}
	}

	public void setAudio() {
		HadalGame.musicPlayer.setVolume(musicVolume * masterVolume);
	}

	/**
	 * a new setting is created if no valid setting is found
	 * This new record has default values for all fields
	 */
	public static void createNewSetting() {
		Setting newSetting = new Setting();
		newSetting.resetDisplay();
		newSetting.resetAudio();
		newSetting.resetServer();
		newSetting.resetMisc();
		newSetting.resetModeSettings();

		Gdx.files.local("save/Settings.json").writeString(GameStateManager.json.prettyPrint(newSetting), false);
	}

	public void resetDisplay() {
		resolution = 1;
		framerate = 1;
		fullscreen = false;
		vsync = false;
		autoIconify = true;
		displayNames = true;
		displayHp = true;
		cursorType = 4;
		cursorSize = 1;
		cursorColor = 4;
		mouseCameraTrack = true;
	}

	public void resetAudio() {
		soundVolume = 1.0f;
		musicVolume = 1.0f;
		masterVolume = 0.75f;
		hitsoundVolume = 0.5f;
		hitsoundType = 1;
	}

	public void resetServer() {
		maxPlayers = 9;
		portNumber = 11100;
		serverPassword = "";
		artifactSlots = 4;
	}

	public void resetMisc() {
		randomNameAlliteration = true;
		consoleEnabled = true;
		verboseDeathMessage = true;
		multiplayerPause = false;
		exportChatLog = false;
		enableUPNP = true;
		hideHUD = false;
	}

	public void resetModeSettings() {
		modeSettings = new HashMap<>();

		for (GameMode mode: GameMode.values()) {
			modeSettings.put(mode.toString(), new HashMap<>());
		}
	}

	public Integer getModeSetting(GameMode mode, String setting, Integer startValue) {
		if (modeSettings.containsKey(mode.toString())) {
			return modeSettings.get(mode.toString()).getOrDefault(setting, startValue);
		} else {
			modeSettings.put(mode.toString(), new HashMap<>());
			return startValue;
		}
	}

	public void setModeSetting(GameMode mode, String setting, Integer value) {
		if (modeSettings.containsKey(mode.toString())) {
			modeSettings.get(mode.toString()).put(setting, value);
		} else {
			HashMap<String, Integer> fug = new HashMap<>();
			fug.put(setting, value);
			modeSettings.put(mode.toString(), fug);
		}
	}

	/**
	 * @return all the parts of this setting that the clients need to know
	 */
	public SharedSetting generateSharedSetting() {
		int pvpTimer = modeSettings.get(GameMode.DEATHMATCH.toString()).getOrDefault(SettingTimer.settingTag, SettingTimer.defaultValue);
		int lives = modeSettings.get(GameMode.DEATHMATCH.toString()).getOrDefault(SettingLives.settingTag, SettingLives.defaultValue);

		return new SharedSetting(maxPlayers, artifactSlots, pvpTimer, lives, multiplayerPause);
	}

	public void setArtifactSlots(int artifactSlots) { this.artifactSlots = artifactSlots; }

	public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

	/**
	 * Convert resolution from index in list to actual setting
	 */
	public void indexToResolution() {
		switch (resolution) {
			case 0 -> Gdx.graphics.setWindowedMode(1024, 576);
			case 1 -> Gdx.graphics.setWindowedMode(1280, 720);
			case 2 -> Gdx.graphics.setWindowedMode(1366, 768);
			case 3 -> Gdx.graphics.setWindowedMode(1600, 900);
			case 4 -> Gdx.graphics.setWindowedMode(1920, 1080);
			case 5 -> Gdx.graphics.setWindowedMode(2560, 1080);
		}
	}

	/**
	 * Convert framerate from index in list to actual framerate
	 */
	public int indexToFramerate() {
		return switch (framerate) {
			case 0 -> 10;
			case 2 -> 90;
			case 3 -> 120;
			case 4 -> 240;
			case 5 -> 0;
			default -> 60;
		};
	}

	public float indexToCursorScale() {
		return switch (cursorSize) {
			case 0 -> 0.5f;
			case 2 -> 1.0f;
			default -> 0.75f;
		};
	}

	public String indexToCursorType() {
		return switch (cursorType) {
			case 1 -> "cursors/crosshair_a.png";
			case 2 -> "cursors/crosshair_b.png";
			case 3 -> "cursors/crosshair_c.png";
			case 4 -> "cursors/crosshair_d.png";
			case 5 -> "cursors/crosshair_e.png";
			case 6 -> "cursors/crosshair_f.png";
			case 7 -> "cursors/crosshair_g.png";
			case 8 -> "cursors/crosshair_h.png";
			case 9 -> "cursors/crosshair_i.png";
			case 10 -> "cursors/crosshair_j.png";
			case 11 -> "cursors/crosshair_k.png";
			case 12 -> "cursors/crosshair_l.png";
			default -> "";
		};
	}

	/**
	 * Convert cursor color from index in list
	 */
	public Color indexToCursorColor() {
		return switch (cursorColor) {
			case 0 -> Color.BLACK;
			case 1 -> Color.CYAN;
			case 2 -> Color.LIME;
			case 3 -> Color.MAGENTA;
			case 4 -> Color.RED;
			case 6 -> Color.YELLOW;
			default -> Color.WHITE;
		};
	}

	/**
	 * Get a sound effect corresponding to the player's currently used hitsound.
	 */
	public SoundEffect indexToHitsound() {
		return indexToHitsound(hitsoundType);
	}

	/**
	 * Get a sound effect corresponding to a certain hitsound. (this is used for previewing hitsounds)
	 */
	public SoundEffect indexToHitsound(int hitsoundType) {
		return switch (hitsoundType) {
			case 1 -> SoundEffect.HITSOUND_BLIP;
			case 3 -> SoundEffect.HITSOUND_DING;
			case 4 -> SoundEffect.HITSOUND_DRUM;
			case 5 -> SoundEffect.HITSOUND_PIANO;
			case 6 -> SoundEffect.HITSOUND_SHREK;
			default -> SoundEffect.HITSOUND_COWBELL;
		};
	}

	public void setResolution(int resolution) { this.resolution = resolution; }

	public void setFramerate(int framerate) { this.framerate = framerate; }

	public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

	public void setVsync(boolean vsync) { this.vsync = vsync; }

	public void setAutoIconify(boolean autoIconify) { this.autoIconify = autoIconify; }

	public void setDisplayNames(boolean displayNames) { this.displayNames = displayNames; }

	public void setDisplayHp(boolean displayHp) { this.displayHp = displayHp; }

	public void setCursorType(int cursorType) {	this.cursorType = cursorType; }

	public void setCursorSize(int cursorSize) { this.cursorSize = cursorSize; }

	public void setCustomShader(int customShader) { this.customShader = customShader; }

	public void setCursorColor(int cursorColor) { this.cursorColor = cursorColor; }

	public void setHitsoundType(int hitsoundType) { this.hitsoundType = hitsoundType; }

	public void setSoundVolume(float soundVolume) {	this.soundVolume = soundVolume; }

	public void setMusicVolume(float musicVolume) {	this.musicVolume = musicVolume;	}

	public void setMasterVolume(float masterVolume) { this.masterVolume = masterVolume; }

	public void setHitsoundVolume(float hitsoundVolume) { this.hitsoundVolume = hitsoundVolume; }

	public void setRandomNameAlliteration(boolean randomNameAlliteration) { this.randomNameAlliteration = randomNameAlliteration; }

	public void setConsoleEnabled(boolean consoleEnabled) { this.consoleEnabled = consoleEnabled; }

	public void setVerboseDeathMessage(boolean verboseDeathMessage) { this.verboseDeathMessage = verboseDeathMessage; }

	public void setMultiplayerPause(boolean multiplayerPause) { this.multiplayerPause = multiplayerPause; }

	public void setExportChatLog(boolean exportChatLog) { this.exportChatLog = exportChatLog; }

	public void setEnableUPNP(boolean enableUPNP) { this.enableUPNP = enableUPNP; }

	public void setHideHUD(boolean hideHUD) { this.hideHUD = hideHUD; }

	public void setMouseCameraTrack(boolean mouseCameraTrack) { this.mouseCameraTrack = mouseCameraTrack; }

	public void setDebugHitbox(boolean debugHitbox) { this.debugHitbox = debugHitbox; }

	public void setPortNumber(int portNumber) { this.portNumber = portNumber; }

	public void setServerPassword(String serverPassword) { this.serverPassword = serverPassword; }

	public int getResolution() { return resolution; }

	public int getFramerate() { return framerate; }

	public boolean isFullscreen() { return fullscreen; }

	public boolean isVSync() { return vsync; }

	public boolean isAutoIconify() { return autoIconify; }

	public int getCursorType() { return cursorType; }

	public int getCursorSize() { return cursorSize; }

	public int getCursorColor() { return cursorColor; }

	public int getCustomShader() { return customShader; }

	public int getHitsound() { return hitsoundType; }

	public float getSoundVolume() {	return soundVolume; }

	public float getMusicVolume() {	return musicVolume; }

	public float getMasterVolume() { return masterVolume; }

	public float getHitsoundVolume() { return hitsoundVolume; }

	public boolean isRandomNameAlliteration() {	return randomNameAlliteration; }

	public boolean isConsoleEnabled() {	return consoleEnabled; }
	
	public boolean isVerboseDeathMessage() { return verboseDeathMessage; }

	public boolean isMultiplayerPause() { return multiplayerPause; }
	
	public boolean isExportChatLog() { return exportChatLog; }

	public boolean isEnableUPNP() { return enableUPNP; }

	public boolean isHideHUD() { return hideHUD; }

	public boolean isMouseCameraTrack() { return mouseCameraTrack; }

	public boolean isDebugHitbox() { return debugHitbox; }

	public boolean isDisplayNames() { return displayNames; }

	public boolean isDisplayHp() { return displayHp; }

	public int getPortNumber() { return portNumber; }
	
	public int getArtifactSlots() { return artifactSlots; }

	public int getMaxPlayers() { return maxPlayers; }

	public String getServerPassword() { return serverPassword; }
}
