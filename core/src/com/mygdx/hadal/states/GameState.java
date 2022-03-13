package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A GameState is any state of the game like a menu or the game screen. The game will keep track of each GameState is currently active
 * to receive input and display information to the player.
 * @author Pratnip Plumbino
 */
public abstract class GameState {
	
	protected final GameStateManager gsm;
	protected final HadalGame app;
	protected final SpriteBatch batch;
	protected final OrthographicCamera camera, hud;
	protected Stage stage;
	
	/**
	 * This constructor is run when the player switches GameState to a new State.
	 * @param gsm: Reference to GameStateManager.
	 */
	public GameState(GameStateManager gsm) {
		this.gsm = gsm;
		this.app = gsm.getApp();
		this.batch = app.getBatch();
		this.camera = app.getCamera();
		this.hud = app.getHud();		
	}
	
	/**
	 * This is run when this state is first added to the top of the stack. This is usually where we set up the scene2d ui elements of the state.
	 */
	public abstract void show();
	
	/**
	 * This will be run every engine tick and will process game logic.
	 * @param delta: elapsed time in seconds since last engine tick.
	 */
	public abstract void update(float delta);
	
	/**
	 * This will be run every engine tick after updating and will display information to the player.
	 * @param delta: elapsed time in seconds since last engine tick.
	 */
	public abstract void render(float delta);
	
	/**
	 * This will be run upon deleting the state and will dispose of any unneeded object in the state. 
	 */
	public abstract void dispose();

	/**
	 * This will be run whenever the game window is resized. 
	 */
	public void resize() {}
	
	public GameStateManager getGsm() {return gsm; }

	public Stage getStage() { return stage; }
	
	public OrthographicCamera getCamera() { return camera; }
	
	public OrthographicCamera getHud() { return hud; }

	public SpriteBatch getBatch() { return batch; }

	/**
	 * Do we process state transitions when this state is active?
	 * (for example, if a pause/setting state is placed over a play state, we don't want a transition when returning to the playstate.)
	 */
	public boolean processTransitions() { return true; }
}
