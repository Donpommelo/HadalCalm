package com.mygdx.hadal.managers;

import java.util.Random;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.Record;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.*;

/**
 * The GameStateManager manages a stack of game states. This delegates logic to the current game state.
 * @author Zachary Tu
 *
 */
public class GameStateManager {
	
	//An instance of the current game
	private HadalGame app;
	
	//Stack of GameStates. These are all the states that the player has opened in that order.
	private Stack<GameState> states;
	
	//temp skin for ui windows
	private Skin skin;
	private NinePatchDrawable dialogPatch, simplePatch;
	private ScrollPaneStyle scrollStyle;
	public static TextureAtlas particleAtlas, projectileAtlas, multitoolAtlas, eventAtlas, explosionAtlas, uiAtlas;
	public static Random generator;
	private Record record;
	
	public static Json json = new Json();
	public static JsonReader reader = new JsonReader();
	
	//This enum lists all the different types of gamestates.
	public enum State {
		SPLASH,
		CONTROL,
		TITLE,
		PLAY, 
		GAMEOVER, 
		VICTORY,
		MENU,
		HUB
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
		UnlockManager.retrieveUnlocks();
		record = json.fromJson(Record.class, reader.parse(Gdx.files.internal("save/Records.json")).toJson(OutputType.minimal));
	}
	
	public void loadAssets() {
		BitmapFont font24 = new BitmapFont();
		this.skin = new Skin();
		this.skin.addRegions((TextureAtlas) HadalGame.assetManager.get(AssetList.UISKINATL.toString()));
		this.skin.add("default-font", font24);
		this.skin.load(Gdx.files.internal("ui/uiskin.json"));
		
		this.dialogPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCHATL.toString())).createPatch("UI_box_dialogue"));
		this.simplePatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCHATL.toString())).createPatch("UI_box_simple"));
		this.scrollStyle = new ScrollPaneStyle(dialogPatch, dialogPatch, dialogPatch, dialogPatch, dialogPatch);
		
		GameStateManager.particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
		GameStateManager.projectileAtlas = HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString());
		GameStateManager.multitoolAtlas = HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
		GameStateManager.eventAtlas = HadalGame.assetManager.get(AssetList.EVENT_ATL.toString());
		GameStateManager.uiAtlas = HadalGame.assetManager.get(AssetList.UI_ATL.toString());
		GameStateManager.explosionAtlas = HadalGame.assetManager.get(AssetList.BOOM_1_ATL.toString());
		generator = new Random();
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
	public void render() {
		states.peek().render();
	}
	
	/**
	 * Run upon deletion (exiting game). This disposes of all states and clears the stack.
	 */
	public void dispose() {
		for (GameState gs : states) {
			gs.dispose();
		}
		states.clear();
		particleAtlas.dispose();
		projectileAtlas.dispose();
		multitoolAtlas.dispose();
		eventAtlas.dispose();
		uiAtlas.dispose();
		explosionAtlas.dispose();
	}
	
	/**
	 * This is run when the window resizes.
	 * @param w: new width of the screen.
	 * @param h: new height of the screen.
	 */
	public void resize(int w, int h) {
		for (Object state : states.toArray()) {
			((GameState) state).resize(w, h);
		};
	}
	
	/**
	 * This is run when we change the current state.
	 * This code adds the new input state, replacing and disposing the previous state if existent.
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
	 * This is a addState eclusively for special playstates.
	 * @param map: level the new playstate will load
	 * @param loadout: loadout that the player will enter the playstate with
	 * @param old: old playerdata to persist stuff like equips/hp/whatever
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public void addPlayState(UnlockLevel map, Loadout loadout, PlayerBodyData old, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(new PlayState(this, loadout, map, true, old));
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(new PlayState(this, loadout, map, true, old));
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
	 * This is called upon adding a new state. It maps each state enum to the actual gameState that will be added to the stack
	 * @param state: enum for the new type of state to be added
	 * @return: A new instance of the gameState corresponding to the input enum
	 */
	public GameState getState(State state) {
		switch(state) {
		case TITLE: return new TitleState(this);
		case SPLASH: return new InitState(this);
		case PLAY: return new PlayState(this, record, true, null);
		case GAMEOVER: return new GameoverState(this);
		case VICTORY: return new VictoryState(this);
		case CONTROL: return new ControlState(this);
		case MENU: return new MenuState(this);
		case HUB: 
			if (record.getFlags().get("INTRO") < 2) {
				return new HubState(this, new Loadout(UnlockEquip.NOTHING));
			} else {
				return new HubState(this, new Loadout(record));
			}
		default:
			break;
		}
		return null;
	}
	
	public HadalGame getApp() {
		return app;
	}
	
	public Record getRecord() {
		return record;
	}

	public Skin getSkin() {
		return skin;
	}
	
	public NinePatchDrawable getDialogPatch() {
		return dialogPatch;
	}
	
	public NinePatchDrawable getSimplePatch() {
		return simplePatch;
	}
	
	public ScrollPaneStyle getScrollStyle() {
		return scrollStyle;
	}
}
