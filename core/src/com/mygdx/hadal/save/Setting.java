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
import com.mygdx.hadal.states.PlayState;

/**
 * A Setting contains all saved game settings.
 * @author Vlurgundy Vluginald
 */
public class Setting {

	private int resolution, framerate, cursorType, cursorSize, cursorColor, maxPlayers, pvpMode, pvpHp, artifactSlots,
		portNumber, hitsoundType, customShader;
	private boolean fullscreen, autoIconify, vsync, debugHitbox, displayNames, displayHp, teamEnabled, randomNameAlliteration,
		consoleEnabled, verboseDeathMessage, multiplayerPause, exportChatLog, enableUPNP, hideHUD;
	private float soundVolume, musicVolume, masterVolume, hitsoundVolume;

	//How long should pvp/coop matches take? (this variable is an index in an array. 0 = infinite, 1 = 60 seconds, 2 = 120 seconds ... etc)
	private int pvpTimer, coopTimer;
	
	//How many lives should players have in pvp? (this variable is an index in an array. 0 = infinite, 1 = 1 life, 2 = 2 lives ... etc)
	private int lives;
	
	//for pvp, how should we give new players loadout? (this variable is an index in an array. 0 = start with default, 1 = start with chosen, 2 = start with random)
	private int loadoutType;

	//connecting clients need to know this password to enter the server
	private String serverPassword;

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

    	//fps is locked to 60 until lag gets sorted out
		//Gdx.graphics.setForegroundFPS(indexToFramerate());

    	game.setAutoIconify(autoIconify);

    	setCursor();
    	
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
	public void setCursor() {

		//when we set a new cursor, we dispose of the old one (if existent)
		if (lastCursor != null) {
			lastCursor.dispose();
		}

		if (cursorType == 0) {
			Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
			lastCursor = null;
		} else {
			Pixmap pm = new Pixmap(indexToCursorSize(), indexToCursorSize(), Pixmap.Format.RGBA8888);
			pm.setColor(indexToCursorColor());
			
			if (cursorType == 1) {
				pm.drawCircle(indexToCursorSize() / 2, indexToCursorSize() / 2, indexToCursorSize() / 4);
				pm.drawLine(0, indexToCursorSize() / 2, indexToCursorSize(), indexToCursorSize() / 2);
				pm.drawLine(indexToCursorSize() / 2, 0, indexToCursorSize() / 2, indexToCursorSize());
			}
			if (cursorType == 2) {
				pm.fillCircle(indexToCursorSize() / 2, indexToCursorSize() / 2, indexToCursorSize() / 3);
			}

			Cursor newCursor = Gdx.graphics.newCursor(pm, indexToCursorSize() / 2, indexToCursorSize() / 2);
	    	Gdx.graphics.setCursor(newCursor);
			lastCursor = newCursor;
	    	pm.dispose();
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
		cursorType = 1;
		cursorSize = 1;
		cursorColor = 4;
	}
	
	public void resetAudio() {
		soundVolume = 1.0f;
		musicVolume = 1.0f;
		masterVolume = 0.75f;
		hitsoundVolume = 0.5f;
		hitsoundType = 1;
	}
	
	public void resetServer() {
		maxPlayers = 7;
		portNumber = 11100;
		serverPassword = "";
		pvpTimer = 3;
		lives = 0;
		teamEnabled = false;
		loadoutType = 3;
		artifactSlots = 4;
		pvpMode = 0;
		pvpHp = 2;
	}
	
	public void resetMisc() {
		coopTimer = 0;
		randomNameAlliteration = true;
		consoleEnabled = true;
		verboseDeathMessage = true;
		multiplayerPause = false;
		exportChatLog = false;
		enableUPNP = true;
		hideHUD = false;
	}

	/**
	 * @return all the parts of this setting that the clients need to know
	 */
	public SharedSetting generateSharedSetting() {
		return new SharedSetting(maxPlayers, pvpMode, artifactSlots, pvpTimer, pvpHp, coopTimer, lives, loadoutType, teamEnabled, multiplayerPause);
	}
	
	public void setPVPTimer(int pvpTimer) { this.pvpTimer = pvpTimer; }
	
	public void setCoopTimer(int coopTimer) { this.coopTimer = coopTimer; }

	public void setLives(int lives) { this.lives = lives; }

	public void setTeamEnabled(boolean teamEnabled) { this.teamEnabled = teamEnabled; }

	public void setLoadoutType(int loadoutType) { this.loadoutType = loadoutType; }
	
	public void setArtifactSlots(int artifactSlots) { this.artifactSlots = artifactSlots; }

	public void setPVPMode(int pvpMode) { this.pvpMode = pvpMode; }

	public void setPVPHp(int pvpHp) { this.pvpHp = pvpHp; }

	public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
	
	/**
	 * Convert resolution from index in list to actual setting
	 */
	public void indexToResolution() {
		switch (resolution) {
		case 0:
			Gdx.graphics.setWindowedMode(1024, 576);
			break;
		case 1:
			Gdx.graphics.setWindowedMode(1280, 720);
			break;
		case 2:
			Gdx.graphics.setWindowedMode(1366, 768);
			break;
		case 3:
			Gdx.graphics.setWindowedMode(1600, 900);
			break;
		case 4:
			Gdx.graphics.setWindowedMode(1920, 1080);
			break;
		case 5:
			Gdx.graphics.setWindowedMode(2560, 1080);
			break;
		}
	}
	
	/**
	 * Convert framerate from index in list to actual framerate
	 */
	public int indexToFramerate() {
		switch (framerate) {
		case 0:
			return 10;
		case 2:
			return 90;
		case 3:
			return 120;
		default:
			return 60;
		}
	}
	
	/**
	 * Convert timer from index in list to actual time amount
	 */
	public static float indexToTimer(int index) {
		switch (index) {
		case 1:
			return 60.0f;
		case 2:
			return 120.0f;
		case 3:
			return 180.0f;
		case 4:
			return 240.0f;
		case 5:
			return 300.0f;
		case 6:
			return 360.0f;
		case 7:
			return 420.0f;
		case 8:
			return 480.0f;
		case 9:
			return 540.0f;
		case 10:
			return 600.0f;
		default:
			return 0.0f;
		}
	}
	
	/**
	 * Convert cursor size from index in list
	 */
	public int indexToCursorSize() {
		switch (cursorSize) {
		case 0:
			return 16;
		case 2:
			return 64;
		default:
			return 32;
		}
	}
	
	/**
	 * Convert cursor color from index in list
	 */
	public Color indexToCursorColor() {
		switch (cursorColor) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.CYAN;
		case 2:
			return Color.LIME;
		case 3:
			return Color.MAGENTA;
		case 4:
			return Color.RED;
		case 6:
			return Color.YELLOW;
		default:
			return Color.WHITE;
		}
	}

	public int indexToHp() {
		switch(pvpHp) {
			case 1:
				return 125;
			case 2:
				return 150;
			case 3:
				return 175;
			case 4:
				return 200;
			default:
				return 100;
		}
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
		switch (hitsoundType) {
		case 1:
			return SoundEffect.HITSOUND_BLIP;
		case 3:
			return SoundEffect.HITSOUND_DING;
		case 4:
			return SoundEffect.HITSOUND_DRUM;
		case 5:
			return SoundEffect.HITSOUND_PIANO;
		case 6:
			return SoundEffect.HITSOUND_SHREK;
		default:
			return SoundEffect.HITSOUND_COWBELL;
		}
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

	public boolean isTeamEnabled() { return teamEnabled; }

	public boolean isRandomNameAlliteration() {	return randomNameAlliteration; }

	public boolean isConsoleEnabled() {	return consoleEnabled; }
	
	public boolean isVerboseDeathMessage() { return verboseDeathMessage; }

	public boolean isMultiplayerPause() { return multiplayerPause; }
	
	public boolean isExportChatLog() { return exportChatLog; }

	public boolean isEnableUPNP() { return enableUPNP; }

	public boolean isHideHUD() { return hideHUD; }

	public boolean isDebugHitbox() { return debugHitbox; }

	public boolean isDisplayNames() { return displayNames; }

	public boolean isDisplayHp() { return displayHp; }

	public int getPortNumber() { return portNumber; }
	
	public int getPVPTimer() { return pvpTimer; }
	
	public int getCoopTimer() { return coopTimer; }

	public int getLives() { return lives; }
	
	public int getLoadoutType() { return loadoutType; }
	
	public int getArtifactSlots() { return artifactSlots; }
	
	public int getPVPMode() { return pvpMode; }

	public int getPVPHp() { return pvpHp; }

	public int getMaxPlayers() { return maxPlayers; }

	public String getServerPassword() { return serverPassword; }
}
