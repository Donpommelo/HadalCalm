package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A GameState is any state of the game like a menu or the game screen. The game will keep track of each GameState is currently active
 * to receive input and display information to the player.
 * @author Zachary Tu
 *
 */
public abstract class GameState {
	
	//References to the Game, StateManager and their relevant fields.
	protected GameStateManager gsm;
	protected HadalGame app;
	protected SpriteBatch batch;
	public OrthographicCamera camera, sprite, hud;
	
	//This is the stage that is displayed for this state
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
		this.sprite = app.getSprite();
		this.hud = app.getHud();		
	}
	
	public void show() {}
	
	/**
	 * This will be run every engine tick and will process game logic.
	 * @param delta: elapsed time in seconds since last engine tick.
	 */
	public abstract void update(float delta);
	
	/**
	 * This will be run every engine tick after updating and will display information to the player.
	 */
	public abstract void render(float delta);
	
	/**
	 * This will be run upon deleting the state and will dispose of any unneeded object in the state. 
	 */
	public abstract void dispose();

	public void resize(int width, int height) {}
	
	public GameStateManager getGsm() {return gsm; }

	public Stage getStage() { return stage; }
}
