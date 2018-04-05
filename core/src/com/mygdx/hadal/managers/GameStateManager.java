package com.mygdx.hadal.managers;

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
	
	//This is the player's currently selected loadout. (not equiped weapons after entering level)
	private Loadout loadout;

	//This is the player's currently selected level filename
	private UnlockLevel level;
	
	private Record record;
	
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
		
		BitmapFont font24 = new BitmapFont();
		this.skin = new Skin();
		this.skin.addRegions((TextureAtlas) HadalGame.assetManager.get(AssetList.UISKINATL.toString()));
		this.skin.add("default-font", font24);
		this.skin.load(Gdx.files.internal("ui/uiskin.json"));
		
		this.dialogPatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCHATL.toString())).createPatch("UI_box_dialogue"));
		this.simplePatch = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCHATL.toString())).createPatch("UI_box_simple"));
		this.scrollStyle = new ScrollPaneStyle(dialogPatch, dialogPatch, dialogPatch, dialogPatch, dialogPatch);
		
		this.loadout = new Loadout();
		this.level = UnlockLevel.ARENA_1;
		
		//Load data from saves: hotkeys and unlocks
		PlayerAction.retrieveKeys();
		
		UnlockManager.retrieveUnlocks();
		
		Json json = new Json();
		JsonReader reader = new JsonReader();
		record = json.fromJson(Record.class, reader.parse(Gdx.files.internal("save/Records.json")).toJson(OutputType.minimal));
	}
	
	/**
	 * Getter for the main game
	 * @return: the game
	 */
	public HadalGame application() {
		return app;
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
	 * TODO: At the moment, we only have one state active. Maybe change later?
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
		case SPLASH: return null;
		case PLAY: return new PlayState(this, loadout, level, true, null);
		case GAMEOVER: return new GameoverState(this);
		case VICTORY: return new VictoryState(this);
		case CONTROL: return new ControlState(this);
		case MENU: return new MenuState(this);
		case HUB: return new HubState(this, loadout);
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

	public Loadout getLoadout() {
		return loadout;
	}

	public UnlockLevel getLevel() {
		return level;
	}

	public void setLevel(UnlockLevel level) {
		this.level = level;
	}
}
