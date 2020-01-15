package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.strategies.shader.*;

public enum Shader {

	NOTHING("", ""),
	WHITE("shaders/pass.vert", "shaders/white.frag"),
	INVERT("shaders/pass.vert", "shaders/invert.frag", new Resolution(), new Timer()),
	SPLASH("shaders/pass.vert", "shaders/splash.frag", new Resolution(), new Timer()),
	DRIP("shaders/pass.vert", "shaders/drip.frag", new Resolution(), new Timer()),
	WAVE("shaders/pass.vert", "shaders/wave.frag", new Resolution(), new Timer()),
	WORM("shaders/pass.vert", "shaders/worm.frag", new Resolution(), new Timer()),
	;
	
	private String vertId, fragId;
	private ShaderProgram shader;
	private ShaderStrategy[] strategies;
	
	Shader(String vertId, String fragId, ShaderStrategy... strategies) {
		this.vertId = vertId;
		this.fragId = fragId;
		this.strategies = strategies;
	}
	
	public void loadShader(PlayState state) {
		
		if (this.equals(NOTHING)) {
			return;
		}
		
		shader = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		shader.begin();
		
		for (ShaderStrategy strat: strategies) {
			strat.create(state, shader);
		}
		
		shader.end();
	}
	
	public void shaderUpdate(PlayState state, float delta) {
		for (ShaderStrategy strat: strategies) {
			strat.controller(state, shader, delta);
		}
	}
	
	public void shaderResize(PlayState state, int width, int height) {
		for (ShaderStrategy strat: strategies) {
			strat.resize(state, shader, width, height);
		}
	}
	
	public ShaderProgram getShader() { return shader; }
}

