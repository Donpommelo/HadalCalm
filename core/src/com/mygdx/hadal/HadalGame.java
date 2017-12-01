package com.mygdx.hadal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.managers.GameStateManager;


public class HadalGame extends ApplicationAdapter {
	
	//Debug 
//	private boolean DEBUG = true;
	
	//Game Info
	public static final String TITLE = "";
	public static final int V_WIDTH = 720;
	public static final int V_HEIGHT = 480;
	private final float SCALE = 2.0f;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;

	private GameStateManager gsm;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		batch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w / SCALE, h / SCALE);
		
		gsm = new GameStateManager(this);		
	}

	@Override
	public void render() {
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) { Gdx.app.exit(); }
	}
	
	@Override
	public void resize (int width, int height) { 
		gsm.resize((int)(width / SCALE), (int)(height / SCALE));
	}
	
	@Override
	public void dispose () {
		gsm.dispose();
		batch.dispose();
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}

}
