package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MessageWindow;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.*;

import java.util.Stack;

/**
 * The GameStateManager manages a stack of game states. This delegates logic to the current game state.
 * For some reason, we are also making it store several public fields like the game record and atlases.
 * @author Fartrand Fucciatello
 */
public class StateManager {

	//Stack of GameStates. These are all the states that the player has opened in that order.
	public static final Stack<GameState> states = new Stack<>();
	
	//are we in single or multiplayer mode?
	public static Mode currentMode = Mode.SINGLE;
	
	/**
	 * Run every engine tick. This delegates to the top state telling it how much time has passed since last update.
	 */
	public static void update(float delta) {
		states.peek().update(delta);
	}
	
	/**
	 * Run every engine tick after updating. This will draw stuff and works pretty much like update.
	 */
	public static void render(float delta) {
		states.peek().render(delta);
	}
	
	/**
	 * Run upon deletion (exiting game). This disposes of all states and clears the stack.
	 */
	public static void dispose() {
		for (GameState gs : states) {
			gs.dispose();
		}
		states.clear();

		HadalGame.assetManager.finishLoading();
		Array<String> assetNames = HadalGame.assetManager.getAssetNames();

		for (String assetName : assetNames) {
			Object asset = HadalGame.assetManager.get(assetName);
			if (asset instanceof TextureAtlas atlas) {
				atlas.dispose();
			}
		}

		clearMemory();
		Particle.disposeParticlePool();
		FrameBufferManager.clearAllFrameBuffers();
	}
	
	/**
	 * This is run when we change the current state.
	 * This code adds the new input state, replacing and disposing the previous state if existent.
	 * Due to states getting more different fields, this should only be used for simple states.
	 * @param state: The new state
	 * @param peekState: the state we are adding on top of. ensures no accidental double-adding
	 */
	public static void addState(HadalGame app, State state, GameState peekState) {
		if (peekState == null) {
			states.push(getState(app, state, null));
			states.peek().show();
		} else {
			addState(getState(app, state, peekState), peekState.getClass());
		}
	}
	
	/**
	 * This is a addState exclusively for special playstates.
	 * @param map: level the new playstate will load
	 * @param mode: the mode of the new map (for maps that are compliant with multiple modes.
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 * @param reset: do we reset player stats in the new play state?
	 * @param startID: the id of the playstate's start point (i.e, if the map has multiple starts, which one do we use?)
	 */
	public static void addPlayState(HadalGame app, UnlockLevel map, GameMode mode, Class<? extends GameState> lastState, boolean reset, String startID) {
		addState(new PlayState(app, map, mode,true, reset, startID), lastState);
	}
	
	/**
	 * Called when game is paused. This adds a PauseState to the stack
	 * @param ps: This is the playstate we are putting the pausestate on
	 * @param pauser: This is the name of the player that paused the game
	 * @param lastState: the state we are adding on top of. ensures no accidental double-adding
	 * @param paused: is the game actually paused underneath the pause menu?
	 */
	public static void addPauseState(PlayState ps, String pauser, Class<? extends GameState> lastState, boolean paused) {
		addState(new PauseState(ps.getApp(), ps, pauser, paused), lastState);
	}

	public static void addState(GameState state, Class<? extends GameState> lastState) {
		if (states.empty()) {
			states.push(state);
			states.peek().show();
		} else if (states.peek().getClass().equals(lastState)) {
			states.push(state);
			states.peek().show();
		}
	}

	public static void removeState(Class<? extends GameState> lastState) {
		removeState(lastState, true);
	}

	/**
	 * Remove the top state from the stack
	 * @param lastState: the state we expect to remove. ensures no double-removing
	 * @param showNext: do we show the state underneath
	 */
	public static void removeState(Class<? extends GameState> lastState, boolean showNext) {
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
	public static void gotoHubState(HadalGame app, Class<? extends GameState> lastState) {
		if (Mode.SINGLE == currentMode) {
			
			//if the player has not done the tutorial yet, they are spawned into the tutorial section. Otherwise; hub
			if (0 == JSONManager.record.getFlags().get("HUB_REACHED")) {
				addPlayState(app, UnlockLevel.WRECK1, GameMode.CAMPAIGN, lastState, true, "");
			} else {
				addPlayState(app, UnlockLevel.SSTUNICATE1, GameMode.HUB, lastState, true, "");
			}
		} else if (Mode.MULTI == currentMode) {
			addPlayState(app, UnlockLevel.HUB_MULTI, GameMode.HUB, lastState, true, "");
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
	public static GameState getState(HadalGame app, State state, GameState peekState) {
        return switch (state) {
            case TITLE -> new TitleState(app);
            case SPLASH -> new InitState(app);
            case ABOUT -> new AboutState(app, peekState);
            case SETTING -> new SettingState(app, peekState);
            case LOBBY -> new LobbyState(app, peekState);
            default -> null;
        };
    }

	/**
	 * 	We clear things like music/sound to free up some memory.
	 * 	This is not really necessary rn as it is not a memory leak, but is probably good practice.
	 */
	public static void clearMemory() {
		MusicTrack.clearMusic();
		SoundEffect.clearSound();
		Shader.clearShader();
		Particle.clearParticle();
	}

	public static void resize() {
		for (GameState gs : states) {
			gs.resize();
		}
	}
	
	/**
	 * This exports the current chat log into a text file.
	 * This is mostly for my own documentation
	 */
	public static void exportChatLogs() {
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
}
