package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.HadalGame;

public enum Shader {

	WHITE("shaders/pass.vert", "shaders/white.frag"),
	INVERT("shaders/pass.vert", "shaders/invert.frag"),
	SPLASH("shaders/pass.vert", "shaders/splash.frag"),
	DRIP("shaders/pass.vert", "shaders/drip.frag"),
	WAVE("shaders/pass.vert", "shaders/wave.frag"),
	WORM("shaders/pass.vert", "shaders/worm.frag"),
	;
	
	private String vertId, fragId;
	private ShaderProgram shader;
	
	Shader(String vertId, String fragId) {
		this.vertId = vertId;
		this.fragId = fragId;
	}
	
	public ShaderProgram getShader() {
		
		shader = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		shader.begin();
		shader.setUniformf("u_resolution", HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
		shader.end();
		
		return shader;
	}
}
