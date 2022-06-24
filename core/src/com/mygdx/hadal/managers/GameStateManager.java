package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MessageWindow;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.Record;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.*;

import java.util.Stack;

/**
 * The GameStateManager manages a stack of game states. This delegates logic to the current game state.
 * For some reason, we are also making it store several public fields like the game record and atlases.
 * @author Fartrand Fucciatello
 */
public class GameStateManager {
	
	//An instance of the current game
	private final HadalGame app;
	
	//Stack of GameStates. These are all the states that the player has opened in that order.
	private final Stack<GameState> states;
	
	//skin for ui windows as well as other patches and atlases. Why are these kept here? Dunno.
	private static Skin skin;
	private static NinePatchDrawable dialogPatch, simplePatch, bossGaugePatch, bossGaugeGreyPatch,
		bossGaugeRedPatch, bossGaugeCatchupPatch;
	
	private static final Array<TextureAtlas> atlases = new Array<>();

	//This is a stored list of all the dialogs/death/misc messages in the game, read from json file.
	public static JsonValue dialogs, deathMessages, shops, randomText, uiStrings, gameStrings, tips;
	
	//These are the player's saved field. These store player info.
	private final Record record;
	private final Setting setting;
	private final SavedLoadout loadout;
	private final SavedOutfits outfits;

	//This contains the settings that are shared with clients (or shared from server if we are the client)
	private SharedSetting sharedSetting, hostSetting;
	
	//Json reader here. Use this instead of creating new ones elsewhere.
	public static final Json json = new Json();
	public static final JsonReader reader = new JsonReader();
	
	//are we in single or multiplayer mode?
	public static Mode currentMode = Mode.SINGLE;
	
	/**
	 * Constructor called by the game upon initialization
	 * @param hadalGame: instance of the current game.
	 */
	public GameStateManager(HadalGame hadalGame) {
		this.app = hadalGame;
		this.states = new Stack<>();

		//we set output settings to json so intellij editor doesn't get pissy about double quotes
		json.setOutputType(OutputType.json);

		//load text strings. Do this before loading saves
		dialogs = reader.parse(Gdx.files.internal("text/Dialogue.json"));
		deathMessages = reader.parse(Gdx.files.internal("text/DeathMessages.json"));
		randomText = reader.parse(Gdx.files.internal("text/RandomText.json"));
		uiStrings = reader.parse(Gdx.files.internal("text/UIStrings.json"));
		gameStrings = reader.parse(Gdx.files.internal("text/GameStrings.json"));
		tips = reader.parse(Gdx.files.internal("text/Tips.json"));
		shops = reader.parse(Gdx.files.internal("save/Shops.json"));

		//Load data from saves: hotkeys, records, loadout, settings and unlocks
		PlayerAction.retrieveKeys();
		record = Record.retrieveRecord();
		loadout = SavedLoadout.retrieveLoadout();
		setting = Setting.retrieveSetting();
		outfits = SavedOutfits.retrieveOutfits();

		//set the game's display to match the player's saved settings
		setting.setDisplay(app, null);
		setting.setCursor();
		sharedSetting = setting.generateSharedSetting();
		hostSetting = setting.generateSharedSetting();

		//this is necessary to prevent nested iterations from causing errors
		Collections.allocateIterators = true;
	}
	
	/**
	 * This loads several assets like atlases, skins and 9patches, particle pool.
	 * This is called by init state after the atlases have been loaded.
	 */
	public void loadAssets() {
		skin = new Skin();
		skin.add("default-font", HadalGame.FONT_UI_SKIN);
		skin.getFont("default-font").getData().setScale(0.25f, 0.25f);
		skin.addRegions(HadalGame.assetManager.get(AssetList.UISKIN_ATL.toString()));
		skin.load(Gdx.files.internal("ui/uiskin.json"));

		dialogPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCH_ATL.toString())).createPatch("UI_box_dialogue"));
		simplePatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCH_ATL.toString())).createPatch("UI_box_simple"));
		
		bossGaugePatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL.toString())).createPatch("boss_gauge"));
		bossGaugeGreyPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL.toString())).createPatch("boss_gauge_grey"));
		bossGaugeRedPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL.toString())).createPatch("boss_gauge_red"));
		bossGaugeCatchupPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL
			.toString())).createPatch("boss_gauge_dark_red"));
		
		atlases.add(HadalGame.assetManager.get(AssetList.PARTICLE_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.FISH_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.TURRET_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.EVENT_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.UI_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.BOOM_1_ATL.toString()));
		atlases.add(HadalGame.assetManager.get(AssetList.IMPACT_ATL.toString()));
		
		//this lets us not declare every attribute of shaders.
		ShaderProgram.pedantic = false;
	}
	
	/**
	 * Run every engine tick. This delegates to the top state telling it how much time has passed since last update.
	 */
	public void update(float delta) {
		states.peek().update(delta);
	}
	
	/**
	 * Run every engine tick after updating. This will draw stuff and works pretty much like update.
	 */
	public void render(float delta) {
		states.peek().render(delta);
	}
	
	/**
	 * Run upon deletion (exiting game). This disposes of all states and clears the stack.
	 */
	public void dispose() {
		for (GameState gs : states) {
			gs.dispose();
		}
		states.clear();
		
		for (TextureAtlas atlas : atlases) {
			atlas.dispose();
		}
		atlases.clear();
		
		if (skin != null) {
			skin.dispose();
		}

		Particle.disposeParticlePool();
	}
	
	/**
	 * This is run when we change the current state.
	 * This code adds the new input state, replacing and disposing the previous state if existent.
	 * Due to states getting more different fields, this should only be used for simple states.
	 * @param state: The new state
	 * @param peekState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addState(State state, GameState peekState) {
		if (states.empty()) {
			states.push(getState(state, peekState));
			states.peek().show();
		} else if (states.peek().getClass().equals(peekState.getClass())) {
			states.push(getState(state, peekState));
			states.peek().show();
		}
	}
	
	/**
	 * This is a addState exclusively for special playstates.
	 * @param map: level the new playstate will load
	 * @param mode: the mode of the new map (for maps that are compliant with multiple modes.
	 * @param loadout: loadout that the player will enter the playstate with
	 * @param old: old playerdata to persist stuff like equips/hp/whatever
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 * @param reset: do we reset player stats in the new play state?
	 * @param startId: the id of the playstate's start point (i.e, if the map has multiple starts, which one do we use?)
	 */
	public void addPlayState(UnlockLevel map, GameMode mode, Loadout loadout, PlayerBodyData old,
							 Class<? extends GameState> lastState, boolean reset, String startId) {
		if (states.empty()) {
			states.push(new PlayState(this, loadout, map, mode,true, old, reset, startId));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new PlayState(this, loadout, map, mode,true, old, reset, startId));
			states.peek().show();
		}
	}
	
	/**
	 * This is called by clients as an addPlayState for ClientStates.
	 * @param map: level the new playstate will load
	 * @param mode: the mode of the new map (for maps that are compliant with multiple modes.
	 * @param loadout: loadout that the player will enter the playstate with
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addClientPlayState(UnlockLevel map, GameMode mode, Loadout loadout, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(new ClientState(this, loadout, map, mode));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new ClientState(this, loadout, map, mode));
			states.peek().show();
		}
	}
	
	/**
	 * Called when game is paused. This adds a PauseState to the stack
	 * @param ps: This is the playstate we are putting the pausestate on
	 * @param pauser: This is the name of the player that paused the game
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 * @param paused: is the game actually paused underneath the pause menu?
	 */
	public void addPauseState(PlayState ps, String pauser, Class<? extends GameState> lastState, boolean paused) {
		if (states.empty()) {
			states.push(new PauseState(this, ps, pauser, paused));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new PauseState(this, ps, pauser, paused));
			states.peek().show();
		}
	}
	
	/**
	 * This is called at the end of levels to display the results of the game
	 * @param ps: This is the playstate we are putting the resultstate on
	 * @param text: this text is displayed at the top of the results state. Declares win or loss (or anything else)
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 * @param fbo: the snapshot of the world in the playstate. used for transitions
	 */
	public void addResultsState(PlayState ps, String text, Class<? extends GameState> lastState, FrameBuffer fbo) {
		if (states.empty()) {
			states.push(new ResultsState(this, text, ps, fbo));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new ResultsState(this, text, ps, fbo));
			states.peek().show();
		}
	}

	public void removeState(Class<? extends GameState> lastState) {
		removeState(lastState, true);
	}

	/**
	 * Remove the top state from the stack
	 * @param lastState: the state we expect to remove. ensures no double-removing
	 * @param showNext: do we show the state underneath
	 */
	public void removeState(Class<? extends GameState> lastState, boolean showNext) {
		if (!states.empty()) {
			if (states.peek().getClass().equals(lastState)) {
				states.pop().dispose();

				//when we remove multiple states at once, we don't want to show after every removal.
				//this is necessary b/c, atm music transitions occur upon showing certain states
				if (showNext) {
					states.peek().show();
				}
			}
		}
	}
	
	/**
	 * This method is just a shortcut for returning to the hub state with a clean loadout
	 */
	public void gotoHubState(Class<? extends GameState> lastState) {
		if (currentMode == Mode.SINGLE) {
			
			//if the player has not done the tutorial yet, they are spawned into the tutorial section. Otherwise; hub
			if (getRecord().getFlags().get("HUB_REACHED").equals(0)) {
				addPlayState(UnlockLevel.WRECK1, GameMode.CAMPAIGN, new Loadout(loadout), null, lastState, true, "");
			} else {
				addPlayState(UnlockLevel.SSTUNICATE1, GameMode.HUB, new Loadout(loadout), null, lastState, true, "");
			}
		} else if (currentMode == Mode.MULTI) {
			addPlayState(UnlockLevel.HUB_MULTI, GameMode.HUB, new Loadout(loadout), null, lastState, true, "");
		}
	}
	
	/**
	 * This is called upon adding a new state. It maps each state enum to the actual gameState that will be added to the stack
	 * @param state: enum for the new type of state to be added
	 * @param peekState: the state underneath this state
	 * @return A new instance of the gameState corresponding to the input enum
	 * NOTE: we no longer use this for any more complicated state that requires extra fields 
	 * Only used for: (TITLE, SPLASH, ABOUT, SETTING, LOBBY)
	 */
	public GameState getState(State state, GameState peekState) {
		switch (state) {
			case TITLE: return new TitleState(this);
			case SPLASH: return new InitState(this);
			case ABOUT: return new AboutState(this, peekState);
			case SETTING: return new SettingState(this, peekState);
			case LOBBY: return new LobbyState(this, peekState);
			default: break;
		}
		return null;
	}

	/**
	 * 	We clear things like music/sound to free up some memory.
	 * 	This is not really necessary rn as it is not a memory leak, but is probably good practice.
	 */
	public static void clearMemory() {
		MusicTrack.clearMusic(HadalGame.musicPlayer);
		SoundEffect.clearSound();
		Shader.clearShader();
		Particle.clearParticle();
	}

	public void resize() {
		for (GameState gs : states) {
			gs.resize();
		}
	}
	
	/**
	 * This exports the current chat log into a text file.
	 * This is mostly for my own documentation
	 */
	public void exportChatLogs() {
		for (String s : MessageWindow.getTextRecord()) {
			Gdx.files.local("save/ChatLog.json").writeString(s + " \n", true);
		}
	}
	
	//This enum lists all the different types of gamestates.
	public enum State {
		SPLASH,
		TITLE,
		SETTING,
		PLAY, 
		VICTORY,
		PAUSE,
		CLIENTPLAY,
		ABOUT,
		LOBBY
	}
	
	//These are the modes of the game
	public enum Mode {
		SINGLE,
		MULTI
	}
		
	public Stack<GameState> getStates() { return states; }

	public HadalGame getApp() { return app;	}
	
	public Record getRecord() {	return record; }

	public SavedLoadout getLoadout() { return loadout; }
	
	public Setting getSetting() { return setting; }

	public SavedOutfits getSavedOutfits() { return outfits; }

	public SharedSetting getSharedSetting() { return sharedSetting; }
	
	public void setSharedSetting(SharedSetting sharedSetting) { this.sharedSetting = sharedSetting; }
	
	public SharedSetting getHostSetting() { return hostSetting; }
	
	public void setHostSetting(SharedSetting hostSetting) { this.hostSetting = hostSetting; }
	
	public static Skin getSkin() { return skin; }
	
	public static NinePatchDrawable getDialogPatch() {	return dialogPatch;	}
	
	public static NinePatchDrawable getSimplePatch() { return simplePatch; }

	public static NinePatchDrawable getBossGaugePatch() { return bossGaugePatch; }

	public static NinePatchDrawable getBossGaugeGreyPatch() { return bossGaugeGreyPatch; }

	public static NinePatchDrawable getBossGaugeRedPatch() { return bossGaugeRedPatch; }
	
	public static NinePatchDrawable getBossGaugeCatchupPatch() { return bossGaugeCatchupPatch; }	
}
