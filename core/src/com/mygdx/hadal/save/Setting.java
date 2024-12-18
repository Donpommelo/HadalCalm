package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.states.PlayState;

import java.util.HashMap;

import static com.mygdx.hadal.managers.JSONManager.JSON;
import static com.mygdx.hadal.managers.JSONManager.READER;

/**
 * A Setting contains all saved game settings.
 * @author Vlurgundy Vluginald
 */
public class Setting {

	private int screen, resolution, framerate, cursorType, cursorSize, cursorColor, maxPlayers, artifactSlots,
		hitsoundType, customShader;
	private boolean mouseRestrict, autoIconify, vsync, debugHitbox, displayNames, displayHp, randomNameAlliteration,
		consoleEnabled, verboseDeathMessage, multiplayerPause, exportChatLog, enableUPNP, hideHUD, mouseCameraTrack, screenShake,
		returnToHubOnReady;
	private float soundVolume, musicVolume, masterVolume, hitsoundVolume;

	//connecting clients need to know this password to enter the server
	private String serverPassword;

	//list of mode-specific settings for each mode
	private HashMap<String, HashMap<String, Integer>> modeSettings;

	public Setting() {}

	/**
	 * This simply saves the settings in a designated file
	 */
	public void saveSetting() {
		Gdx.files.local("save/Settings.json").writeString(JSON.prettyPrint(this), false);
	}

	/**
	 * This retrieves the player's setting at the start of the game
	 * @return the player's setting (or default setting if file is missing or malformed)
	 */
	public static Setting retrieveSetting() {
		Setting tempSetting;
		try {
			tempSetting = JSON.fromJson(Setting.class, READER.parse(Gdx.files.local("save/Settings.json")).toJson(JsonWriter.OutputType.json));
		} catch (SerializationException | IllegalArgumentException e) {
			Setting.createNewSetting();
			tempSetting = JSON.fromJson(Setting.class, READER.parse(Gdx.files.local("save/Settings.json")).toJson(JsonWriter.OutputType.json));
		}
		return tempSetting;
	}

	/**
	 * This sets display settings, changing screen size/vsync/framerate
	 */
	public void setDisplay(HadalGame game, PlayState state, boolean screenChanged) {
		Monitor currMonitor = Gdx.graphics.getMonitor();
    	DisplayMode displayMode = Gdx.graphics.getDisplayMode(currMonitor);

		if (screenChanged) {
			switch (screen) {
				case 1:
					Gdx.graphics.setUndecorated(true);
					Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
					return;
				case 2:
					Gdx.graphics.setUndecorated(false);
					indexToResolution();
					Gdx.graphics.setVSync(false);
					return;
				default:
					Gdx.graphics.setFullscreenMode(displayMode);
					Gdx.graphics.setVSync(vsync);
					return;
			}
		}

		Gdx.graphics.setForegroundFPS(indexToFramerate());

    	game.setAutoIconify(autoIconify);

    	if (state != null) {
			if (state.getRenderManager() != null) {
				state.getRenderManager().getWorldManager().toggleVisibleHitboxes(debugHitbox);
			}
    	}

		//resizing here (possibly) deals with some fullscreen camera issues on certain devices?
		game.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void setAudio() {
		MusicPlayer.setVolume(musicVolume * masterVolume);
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
		newSetting.resetModeSettings();

		Gdx.files.local("save/Settings.json").writeString(JSON.prettyPrint(newSetting), false);
	}

	public void resetDisplay() {
		resolution = 1;
		framerate = 1;
		screen = 0;
		vsync = false;
		mouseRestrict = true;
		autoIconify = true;
		displayNames = true;
		displayHp = true;
		cursorType = 6;
		cursorSize = 0;
		cursorColor = 4;
		mouseCameraTrack = true;
		screenShake = true;
		returnToHubOnReady = true;
	}

	public void resetAudio() {
		soundVolume = 1.0f;
		musicVolume = 1.0f;
		masterVolume = 0.5f;
		hitsoundVolume = 0.5f;
		hitsoundType = 1;
	}

	public void resetServer() {
		maxPlayers = 9;
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

		for (GameMode mode : GameMode.values()) {
			modeSettings.put(mode.toString(), new HashMap<>());
		}
	}

	public Integer getModeSetting(GameMode mode, SettingSave setting) {
		return getModeSetting(mode, setting, setting.getStartingValue());
	}

	/**
	 * This retrieves a setting specific to a single mode
	 */
	public Integer getModeSetting(GameMode mode, SettingSave setting, Integer startValue) {

		//in arcade mode, we use the default value for setting, unless there is a specific override value
		if (SettingArcade.arcade) {
			if (SettingArcade.currentMode != null) {
				if (SettingArcade.currentMode.getUniqueSettings().containsKey(setting.name())) {
					return SettingArcade.currentMode.getUniqueSettings().get(setting.name());
				}
				if (modeSettings.get(mode.name()).containsKey(setting.name())) {
					return modeSettings.get(mode.name()).get(setting.name());
				}
				return setting.getStartingValue();
			}
		}

		if (modeSettings.containsKey(mode.name())) {
			return modeSettings.get(mode.name()).getOrDefault(setting.name(), startValue);
		} else {
			modeSettings.put(mode.name(), new HashMap<>());
			return startValue;
		}
	}

	/**
	 * This sets a setting specific to a single mode
	 */
	public void setModeSetting(GameMode mode, SettingSave setting, Integer value) {
		if (modeSettings.containsKey(mode.toString())) {
			modeSettings.get(mode.toString()).put(setting.name(), value);
		} else {
			HashMap<String, Integer> fug = new HashMap<>();
			fug.put(setting.name(), value);
			modeSettings.put(mode.toString(), fug);
		}
	}

	/**
	 * @return all the parts of this setting that the clients need to know
	 */
	public SharedSetting generateSharedSetting() {
		return new SharedSetting(maxPlayers, artifactSlots, multiplayerPause);
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
			case 0 -> 30;
			case 2 -> 90;
			case 3 -> 120;
			case 4 -> 240;
			case 5 -> 0;
			default -> 60;
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

	public HashMap<String, Integer> getModeSettings(GameMode mode) {
		return modeSettings.getOrDefault(mode.toString(), null);
	}

	public void setResolution(int resolution) { this.resolution = resolution; }

	public void setFramerate(int framerate) { this.framerate = framerate; }

	public void setScreen(int screen) { this.screen = screen; }

	public void setMouseRestrict(boolean mouseRestrict) { this.mouseRestrict = mouseRestrict; }

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

	public void setScreenShake(boolean screenShake) { this.screenShake = screenShake; }

	public void setDebugHitbox(boolean debugHitbox) { this.debugHitbox = debugHitbox; }

	public void setReturnToHubOnReady(boolean returnToHubOnReady) { this.returnToHubOnReady = returnToHubOnReady; }

	public void setServerPassword(String serverPassword) { this.serverPassword = serverPassword; }

	public int getScreen() { return screen; }

	public int getResolution() { return resolution; }

	public int getFramerate() { return framerate; }

	public boolean isMouseRestrict() { return mouseRestrict; }

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

	public boolean isScreenShake() { return screenShake; }

	public boolean isReturnToHubOnReady() {	return returnToHubOnReady; }

	public boolean isDebugHitbox() { return debugHitbox; }

	public boolean isDisplayNames() { return displayNames; }

	public boolean isDisplayHp() { return displayHp; }

	public int getArtifactSlots() { return artifactSlots; }

	public int getMaxPlayers() { return maxPlayers; }

	public String getServerPassword() { return serverPassword; }
}
