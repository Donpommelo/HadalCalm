package com.mygdx.hadal.managers;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.Record;
import com.mygdx.hadal.save.Setting;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.*;

/**
 * The GameStateManager manages a stack of game states. This delegates logic to the current game state.
 * For some reason, we are also making it stores everal public fields like the game record and atlases.
 * @author Zachary Tu
 *
 */
public class GameStateManager {
	
	//An instance of the current game
	private HadalGame app;
	
	//Stack of GameStates. These are all the states that the player has opened in that order.
	private Stack<GameState> states;
	
	//skin for ui windows as well as other patches and atlases. Why are these kept here? Dunno.
	private Skin skin;
	private NinePatchDrawable dialogPatch, simplePatch, bossGaugePatch, bossGaugeGreyPatch, bossGaugeRedPatch, bossGaugeCatchupPatch;
	
	private static ArrayList<TextureAtlas> atlases = new ArrayList<TextureAtlas>();
	public static TextureAtlas projectileAtlas, multitoolAtlas, fishAtlas, turretAtlas, eventAtlas, explosionAtlas, uiAtlas;
	public static TextureAtlas particleAtlas, exclamationAtlas, impactAtlas, starShotAtlas;
	
	//This is a stored list of all the dialog in the game, read from json file.
	private static JsonValue dialogs;
	
	//This is the player's record. This stores player info.
	private Record record;
	private Setting setting;
	private static JsonValue shops;
	
	//Json reader here. Use this instead of creating new ones elsewhere.
	public static Json json = new Json();
	public static JsonReader reader = new JsonReader();
	
	//Not sure if this is a sensible thing to do, but we have an rng here so I don't need to make one whenever elsewhere
	public static Random generator;
	
	//are we in single or multiplayer mode?
	public static Mode currentMode = Mode.SINGLE;
	
	//This enum lists all the different types of gamestates.
	public enum State {
		SPLASH,
		CONTROL,
		TITLE,
		PLAY, 
		VICTORY,
		PAUSE,
		CLIENTPLAY
	}
	
	//These are the modes of the game
	public enum Mode {
		SINGLE,
		MULTI
	}
	
	/**
	 * Constructor called by the game upon initialization
	 * @param hadalGame: instance of the current game.
	 */
	public GameStateManager(HadalGame hadalGame) {
		this.app = hadalGame;
		this.states = new Stack<GameState>();
		
		//Load data from saves: hotkeys and unlocks
		PlayerAction.retrieveKeys();
		UnlockManager.retrieveItemInfo();
		if (!Gdx.files.internal("save/Records.json").exists()) {
			Record.createNewRecord();
		}
		if (!Gdx.files.internal("save/Settings.json").exists()) {
			Setting.createNewSetting();
		}
		
		//Load player records and game dialogs, also from json
		record = json.fromJson(Record.class, reader.parse(Gdx.files.internal("save/Records.json")).toJson(OutputType.minimal));
		setting = json.fromJson(Setting.class, reader.parse(Gdx.files.internal("save/Settings.json")).toJson(OutputType.minimal));
		dialogs = reader.parse(Gdx.files.internal("text/Dialogue.json"));
		shops = reader.parse(Gdx.files.internal("save/Shops.json"));
		
		generator = new Random();
		
		setting.setDisplay(app);
	}
	
	/**
	 * This loads several assets like atlases, skins and patches.
	 * This is called by initmanager after the atlases have been loaded.
	 */
	public void loadAssets() {
		BitmapFont font24 = new BitmapFont();
		this.skin = new Skin();
		this.skin.addRegions((TextureAtlas) HadalGame.assetManager.get(AssetList.UISKINATL.toString()));
		this.skin.add("default-font", font24);
		this.skin.load(Gdx.files.internal("ui/uiskin.json"));
		
		this.dialogPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCHATL.toString())).createPatch("UI_box_dialogue"));
		this.simplePatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCHATL.toString())).createPatch("UI_box_simple"));
		
		this.bossGaugePatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGEATLAS.toString())).createPatch("boss_gauge"));
		this.bossGaugeGreyPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGEATLAS.toString())).createPatch("boss_gauge_grey"));
		this.bossGaugeRedPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGEATLAS.toString())).createPatch("boss_gauge_red"));
		this.bossGaugeCatchupPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGEATLAS.toString())).createPatch("boss_gauge_dark_red"));
		
		atlases.add(GameStateManager.particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString()));
		
		atlases.add(GameStateManager.projectileAtlas = HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString()));
		atlases.add(GameStateManager.multitoolAtlas = HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString()));
		atlases.add(GameStateManager.fishAtlas = HadalGame.assetManager.get(AssetList.FISH_ATL.toString()));
		atlases.add(GameStateManager.turretAtlas = HadalGame.assetManager.get(AssetList.TURRET_ATL.toString()));
		atlases.add(GameStateManager.eventAtlas = HadalGame.assetManager.get(AssetList.EVENT_ATL.toString()));
		atlases.add(GameStateManager.uiAtlas = HadalGame.assetManager.get(AssetList.UI_ATL.toString()));
		atlases.add(GameStateManager.explosionAtlas = HadalGame.assetManager.get(AssetList.BOOM_1_ATL.toString()));
		
		atlases.add(GameStateManager.exclamationAtlas = HadalGame.assetManager.get(AssetList.EXCLAMATION_ATLAS.toString()));
		atlases.add(GameStateManager.impactAtlas = HadalGame.assetManager.get(AssetList.IMPACT_ATLAS.toString()));
		atlases.add(GameStateManager.starShotAtlas = HadalGame.assetManager.get(AssetList.STAR_SHOT_ATLAS.toString()));
		
		Particle.initParticlePool();
		ShaderProgram.pedantic = false;
	}
	
	/**
	 * Run every engine tick. This delegates to the top state telling it how much time has passed since last update.
	 * @param delta: elapsed time in seconds since last engine tick.
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
		
		for (TextureAtlas atlas: atlases) {
			atlas.dispose();
		}
		atlases.clear();
	}
	
	/**
	 * This is run when we change the current state.
	 * This code adds the new input state, replacing and disposing the previous state if existent.
	 * Due to states getting more different fields, this should only be used for simple states.
	 * @param state: The new state
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addState(State state, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(getState(state));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(getState(state));
			states.peek().show();
		}
	}
	
	/**
	 * This is a addState exclusively for special playstates.
	 * @param map: level the new playstate will load
	 * @param loadout: loadout that the player will enter the playstate with
	 * @param old: old playerdata to persist stuff like equips/hp/whatever
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 * @param reset: do we reset player stats in the new play state?
	 * @param startId: the id of the playstate's start point (i.e, if the map has multiple starts, which one do we use?)
	 */
	public void addPlayState(UnlockLevel map, Loadout loadout, PlayerBodyData old, Class<? extends GameState> lastState, boolean reset, String startId) {
		
		if (states.empty()) {
			states.push(new PlayState(this, loadout, map, true, old, reset, startId));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new PlayState(this, loadout, map, true, old, reset, startId));
			states.peek().show();
		}
	}
	
	/**
	 * This is called by clients as an addPlayState for ClientStates.
	 * @param map: level the new playstate will load
	 * @param loadout: loadout that the player will enter the playstate with
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addClientPlayState(UnlockLevel map, Loadout loadout, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(new ClientState(this, loadout, map));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new ClientState(this, loadout, map));
			states.peek().show();
		}
	}
	
	/**
	 * Called when game is paused. This adds a PauseState to the stack
	 * @param ps: This is the playstate we are putting the pausestate on
	 * @param pauser: This is the name of the player that paused the game
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addPauseState(PlayState ps, String pauser, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(new PauseState(this, ps, pauser));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new PauseState(this, ps, pauser));
			states.peek().show();
		}
	}
	
	/**
	 * Called when game setting menu is pulled up. This adds a ControlState to the stack
	 * @param ps: This is the playstate we are putting the control state on
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addSettingState(PlayState ps, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(new SettingState(this, ps));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new SettingState(this, ps));
			states.peek().show();
		}
	}
	
	/**
	 * This is called at the end of levels to display the results of the game
	 * @param ps: This is the playstate we are putting the resultsstate on
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addResultsState(PlayState ps, String text, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(new ResultsState(this, text, ps));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new ResultsState(this, text, ps));
			states.peek().show();
		}
	}
	
	/**
	 * Remove the top state from the stack
	 * @param lastState: the state we expect to remove. ensures no double-removing
	 */
	public void removeState(Class<? extends GameState> lastState) {
		if (!states.empty()) {
			if (states.peek().getClass().equals(lastState)) {
				states.pop().dispose();
				states.peek().show();
			}
		}
	}
	
	/**
	 * This method is just a shortcut for returning to the hub state with a clean loadout
	 */
	public void gotoHubState() {
		if (currentMode == Mode.SINGLE) {
			addPlayState(UnlockLevel.HUB, new Loadout(record), null, TitleState.class, true, "");
		}
		if (currentMode == Mode.MULTI) {
			addPlayState(UnlockLevel.HUB_MULTI, new Loadout(record), null, TitleState.class, true, "");
		}
	}
	
	/**
	 * This is called upon adding a new state. It maps each state enum to the actual gameState that will be added to the stack
	 * @param state: enum for the new type of state to be added
	 * @return: A new instance of the gameState corresponding to the input enum
	 * NOTE: we no longer use this for any more complicated state that requires extra fields 
	 * Only used for: (TITLE, SPLASH)
	 */
	public GameState getState(State state) {
		switch(state) {
		case TITLE: return new TitleState(this);
		case SPLASH: return new InitState(this);
		default:
			break;
		}
		return null;
	}
	
	public void resize(int width, int height) {
		for (GameState gs : states) {
			gs.resize(width, height);
		}
	}
	
	public void finishTransition() {
		if (!states.empty()) {
			states.peek().transitionState();
			app.fadeIn();
		}
	}
	
	public Stack<GameState> getStates() { return states; }

	public HadalGame getApp() { return app;	}
	
	public static JsonValue getDialogs() { return dialogs; }

	public static JsonValue getShops() { return shops; }
	
	public Record getRecord() {	return record; }

	public Setting getSetting() { return setting; }
	
	public Skin getSkin() {	return skin; }
	
	public NinePatchDrawable getDialogPatch() {	return dialogPatch;	}
	
	public NinePatchDrawable getSimplePatch() { return simplePatch; }

	public NinePatchDrawable getBossGaugePatch() { return bossGaugePatch; }

	public NinePatchDrawable getBossGaugeGreyPatch() { return bossGaugeGreyPatch; }

	public NinePatchDrawable getBossGaugeRedPatch() { return bossGaugeRedPatch; }
	
	public NinePatchDrawable getBossGaugeCatchupPatch() { return bossGaugeCatchupPatch; }	
}
