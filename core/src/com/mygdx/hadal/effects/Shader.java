package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public enum Shader {

	WHITE("shaders/pass.vert", "shaders/white.frag"),
	INVERT("shaders/pass.vert", "shaders/invert.frag"),
	;
	
	private String vertId, fragId;
	private ShaderProgram shader;
	
	Shader(String vertId, String fragId) {
		this.vertId = vertId;
		this.fragId = fragId;
	}
	
	public ShaderProgram getShader() {
		
		if (shader == null) {
			shader = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		}
		return shader;
	}
}
