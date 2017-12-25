package com.mygdx.hadal.managers;

import java.util.Stack;

import com.mygdx.hadal.HadalGame;
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
	
	//This enum lists all the different types of gamestates.
	public enum State {
		SPLASH,
		TITLE,
		PLAY
	}
	
	/**
	 * Constructor called by the game upon initialization
	 * @param hadalGame: instance of the current game.
	 */
	public GameStateManager(HadalGame hadalGame) {
		this.app = hadalGame;
		this.states = new Stack<GameState>();
		
		//Default state is the splash state currently.
		this.setState(State.SPLASH);
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
		states.peek().resize(w, h);
	}
	
	/**
	 * This is run when we change the current state.
	 * TODO: At the moment, we only have one state active. Maybe change later?
	 * This code adds the new input state, replacing and disposing the previous state if existant.
	 * @param state: The new state
	 */
	public void setState(State state) {
		if (states.size() >= 1) {
			states.pop().dispose();
		}
		states.push(getState(state));
	}
	
	/**
	 * This is called upon adding a new state. It maps each state enum to the actual gameState that will be added to the stack
	 * @param state: enum for the new type of state to be added
	 * @return: A new instance of the gameState corresponding to the input enum
	 */
	public GameState getState(State state) {
		switch(state) {
		case SPLASH: return new SplashState(this);
		case TITLE: return null;
		case PLAY: return new PlayState(this);
		}
		return null;
	}
}
