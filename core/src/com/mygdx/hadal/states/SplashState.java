package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.hadal.managers.GameStateManager;

public class SplashState extends GameState{

	float acc = 0f;
	Texture tex;
	
	public SplashState(GameStateManager gsm) {
		super(gsm);
		tex = new Texture("Test/dummy_side_mirror.gif");
	}

	@Override
	public void update(float delta) {
		acc+=delta;
		if (acc >= 1) {
			gsm.setState(GameStateManager.State.PLAY);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(tex, Gdx.graphics.getWidth() / 4 - tex.getWidth() / 2, Gdx.graphics.getHeight() / 4 - tex.getHeight() / 2);
		batch.end();
	}

	@Override
	public void dispose() {
		tex.dispose();
	}

}
