package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A Setting contains all saved game settings.
 * @author Zachary Tu
 *
 */
public class Setting {

	private int resolution;
	private int framerate;
	private boolean fullscreen;
	private boolean vsync;
	private float soundVolume;
	private float musicVolume;
	private float masterVolume;
	private boolean randomNameAlliteration;
	private boolean consoleEnabled;

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
	}
	
	/**
	 * a new setting is created if no valid setting is found
	 * This new record has default values for all fields
	 */
	public static void createNewSetting() {
		Setting newSetting = new Setting();
		newSetting.resetDisplay();
		newSetting.resetAudio();
		
		Gdx.files.local("save/Settings.json").writeString(GameStateManager.json.prettyPrint(newSetting), false);
	}
	
	public void resetDisplay() {
		resolution = 1;
		framerate = 1;
		fullscreen = false;
		vsync = false;
	}
	
	public void resetAudio() {
		soundVolume = 0.5f;
		musicVolume = 0.5f;
		masterVolume = 0.5f;
	}
	
	public void resetGameplay() {
		timer = 0;
		lives = 1;
		loadoutType = 0;
	}
	
	public void resetMisc() {
		randomNameAlliteration = true;
		consoleEnabled = true;
	}
	
	public void setTimer(int timer) {
		this.timer = timer;
		saveSetting();
	}
	
	public void setLives(int lives) {
		this.lives = lives;
		saveSetting();
	}
	
	public void setLoadoutType(int loadoutType) {
		this.loadoutType = loadoutType;
		saveSetting();
	}
	
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
	
	public void setResolution(int resolution) { this.resolution = resolution; }

	public void setFramerate(int framerate) { this.framerate = framerate; }
	
	public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

	public void setVsync(boolean vsync) { this.vsync = vsync; }
		
	public void setSoundVolume(float soundVolume) {	this.soundVolume = soundVolume; }

	public void setMusicVolume(float musicVolume) {	this.musicVolume = musicVolume;	}

	public void setMasterVolume(float masterVolume) { this.masterVolume = masterVolume; }

	public void setRandomNameAlliteration(boolean randomNameAlliteration) { this.randomNameAlliteration = randomNameAlliteration; }
	
	public void setConsoleEnabled(boolean consoleEnabled) { this.consoleEnabled = consoleEnabled; }

	public int getResolution() { return resolution; }
	
	public int getFramerate() { return framerate; }
	
	public boolean isFullscreen() { return fullscreen; }
	
	public boolean isVSync() { return vsync; }
	
	public float getSoundVolume() {	return soundVolume; }

	public float getMusicVolume() {	return musicVolume; }

	public float getMasterVolume() { return masterVolume; }
	
	public boolean isRandomNameAlliteration() {	return randomNameAlliteration; }
	
	public boolean isConsoleEnabled() {	return consoleEnabled; }

	public int getTimer() { return timer; }
	
	public int getLives() { return lives; }
	
	public int getLoadoutType() { return loadoutType; }
}
