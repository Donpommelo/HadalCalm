package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;

public abstract class GameState {
	
	//References
	protected GameStateManager gsm;
	protected HadalGame app;
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	
	public GameState(GameStateManager gsm) {
		this.gsm = gsm;
		this.app = gsm.application();
		this.batch = app.getBatch();
		this.camera = app.getCamera();
	}
	
	public void resize(int w, int h) {
		camera.setToOrtho(false, w, h);
	}
	
	public abstract void update(float delta);
	public abstract void render();
	public abstract void dispose();
}
