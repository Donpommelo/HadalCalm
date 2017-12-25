package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The SplashState is created upon initializing the game and will display an image briefly before switching to the main game state.
 * TODO: Eventually, this might be where we initialize game data + assets.
 * @author Zachary Tu
 *
 */
public class SplashState extends GameState{

	//This counter keeps track of how long this state is active.
	private float acc = 0f;
	
	//This is a tentative image to be displayed.
	private Texture tex;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public SplashState(GameStateManager gsm) {
		super(gsm);
		tex = new Texture("Test/dummy_side_mirror.gif");
	}

	/**
	 * Every tick, this state will increment the timer until 1 second passes in which it transitions to the game state.
	 */
	@Override
	public void update(float delta) {
		acc+=delta;
		if (acc >= 1) {
			gsm.setState(GameStateManager.State.PLAY);
		}
	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(tex, Gdx.graphics.getWidth() / 4 - tex.getWidth() / 2, Gdx.graphics.getHeight() / 4 - tex.getHeight() / 2);
		batch.end();
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		tex.dispose();
	}

}
