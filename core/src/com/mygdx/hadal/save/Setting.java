package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A Setting contains all saved game settings.
 * @author Zachary Tu
 *
 */
public class Setting {

	private int resolution, framerate, cursorType, cursorSize, cursorColor, maxPlayers, pvpMode, artifactSlots, portNumber;
	private boolean fullscreen, vsync, randomNameAlliteration, consoleEnabled, verboseDeathMessage, clientPause;
	private float soundVolume, musicVolume, masterVolume;

	//How long should pvp matches take? (this variable is an index in an array. 0 = infinite, 1 = 60 seconds, 2 = 120 seconds ... etc)
	private int timer;
	
	//How many lives should players have in pvp? (this variable is an index in an array. 0 = infinite, 1 = 1 life, 2 = 2 lives ... etc)
	private int lives;
	
	//for pvp, how should we give new players loadout? (this variable is an index in an array. 0 = start with default, 1 = start with chosen, 2 = start with random)
	private int loadoutType;
	
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
	public void setDisplay(HadalGame game) {
		Monitor currMonitor = Gdx.graphics.getMonitor();
    	DisplayMode displayMode = Gdx.graphics.getDisplayMode(currMonitor);
    	
    	if (fullscreen) {
    		Gdx.graphics.setFullscreenMode(displayMode);
    	} else {
    		indexToResolution();
    	}
    	
    	Gdx.graphics.setVSync(vsync);
    	
    	game.setFrameRate(indexToFramerate());
    	
    	setCursor();
	}
	
	/**
	 * This sets the player's cursor according to their saved settings
	 * cursorType == 0: default cursor
	 * cursorType == 1: crosshair cursor
	 * cursorType == 2: dot cursor
	 */
	public void setCursor() {
		if (cursorType == 0) {
			Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
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
			
	    	Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, indexToCursorSize() / 2, indexToCursorSize() / 2));
	    	pm.dispose();
		}
	}
	
	/**
	 * a new setting is created if no valid setting is found
	 * This new record has default values for all fields
	 */
	public static void createNewSetting() {
		Setting newSetting = new Setting();
		newSetting.resetDisplay();
		newSetting.resetAudio();
		newSetting.resetGameplay();
		newSetting.resetMisc();
		
		Gdx.files.local("save/Settings.json").writeString(GameStateManager.json.prettyPrint(newSetting), false);
	}
	
	public void resetDisplay() {
		resolution = 1;
		framerate = 3;
		fullscreen = false;
		vsync = false;
		cursorType = 1;
		cursorSize = 1;
		cursorColor = 4;
	}
	
	public void resetAudio() {
		soundVolume = 1.0f;
		musicVolume = 1.0f;
		masterVolume = 1.0f;
	}
	
	public void resetGameplay() {
		timer = 3;
		lives = 0;
		loadoutType = 1;
		artifactSlots = 4;
		pvpMode = 0;
	}
	
	public void resetMisc() {
		randomNameAlliteration = true;
		consoleEnabled = true;
		verboseDeathMessage = true;
		clientPause = true;
		maxPlayers = 4;
		portNumber = 11100;
	}
	
	public void setTimer(int timer) { this.timer = timer; }
	
	public void setLives(int lives) { this.lives = lives; }
	
	public void setLoadoutType(int loadoutType) { this.loadoutType = loadoutType; }
	
	public void setArtifactSlots(int artifactSlots) { this.artifactSlots = artifactSlots; }

	public void setPVPMode(int pvpMode) { this.pvpMode = pvpMode; }

	public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
	
	/**
	 * Convert resolution from index in list to actual setting
	 */
	public void indexToResolution() {
		switch(resolution) {
		case 0:
			Gdx.graphics.setWindowedMode(1024, 576);
			break;
		case 1:
			Gdx.graphics.setWindowedMode(1280, 720);
			break;
		case 2:
			Gdx.graphics.setWindowedMode(1600, 900);
			break;
		case 3:
			Gdx.graphics.setWindowedMode(1920, 1080);
			break;
		}
	}
	
	/**
	 * Convert framerate from index in list to actual framerate
	 */
	public int indexToFramerate() {
		switch(framerate) {
		case 0:
			return 30;
		case 1:
			return 60;
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
		switch(index) {
		case 0:
			return 0.0f;
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
		default:
			return 0.0f;
		}
	}
	
	/**
	 * Convert cursor size from index in list
	 */
	public int indexToCursorSize() {
		switch(cursorSize) {
		case 0:
			return 16;
		case 1:
			return 32;
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
		switch(cursorColor) {
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
		case 5:
			return Color.WHITE;
		case 6:
			return Color.YELLOW;
		default:
			return Color.WHITE;
		}
	}
	
	public void setResolution(int resolution) { this.resolution = resolution; }

	public void setFramerate(int framerate) { this.framerate = framerate; }
	
	public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

	public void setVsync(boolean vsync) { this.vsync = vsync; }
	
	public void setCursorType(int cursorType) {	this.cursorType = cursorType; }

	public void setCursorSize(int cursorSize) { this.cursorSize = cursorSize; }

	public void setCursorColor(int cursorColor) { this.cursorColor = cursorColor; }

	public void setSoundVolume(float soundVolume) {	this.soundVolume = soundVolume; }

	public void setMusicVolume(float musicVolume) {	this.musicVolume = musicVolume;	}

	public void setMasterVolume(float masterVolume) { this.masterVolume = masterVolume; }

	public void setRandomNameAlliteration(boolean randomNameAlliteration) { this.randomNameAlliteration = randomNameAlliteration; }
	
	public void setConsoleEnabled(boolean consoleEnabled) { this.consoleEnabled = consoleEnabled; }
	
	public void setVerboseDeathMessage(boolean verboseDeathMessage) { this.verboseDeathMessage = verboseDeathMessage; }

	public void setClientPause(boolean clientPause) { this.clientPause = clientPause; }

	public void setPortNumber(int portNumber) { this.portNumber = portNumber; }

	public int getResolution() { return resolution; }
	
	public int getFramerate() { return framerate; }
	
	public boolean isFullscreen() { return fullscreen; }
	
	public boolean isVSync() { return vsync; }
	
	public int getCursorType() { return cursorType; }
	
	public int getCursorSize() { return cursorSize; }
	
	public int getCursorColor() { return cursorColor; }
	
	public float getSoundVolume() {	return soundVolume; }

	public float getMusicVolume() {	return musicVolume; }

	public float getMasterVolume() { return masterVolume; }
	
	public boolean isRandomNameAlliteration() {	return randomNameAlliteration; }
	
	public boolean isConsoleEnabled() {	return consoleEnabled; }
	
	public boolean isVerboseDeathMessage() { return verboseDeathMessage; }

	public boolean isClientPause() { return clientPause; }

	public int getPortNumber() { return portNumber; }
	
	public int getTimer() { return timer; }
	
	public int getLives() { return lives; }
	
	public int getLoadoutType() { return loadoutType; }
	
	public int getArtifactSlots() { return artifactSlots; }
	
	public int getPVPMode() { return pvpMode; }

	public int getMaxPlayers() { return maxPlayers; }
}
