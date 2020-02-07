package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.strategies.shader.*;

public enum Shader {

	NOTHING("", "", true),
	WHITE("shaders/pass.vert", "shaders/white.frag", true),
	INVERT("shaders/pass.vert", "shaders/invert.frag", true, new Resolution(), new Timer()),
	SPLASH("shaders/pass.vert", "shaders/splash.frag", true, new Resolution(), new Timer()),
	DRIP("shaders/pass.vert", "shaders/drip.frag", true, new Resolution(), new Timer()),
	WAVE("shaders/pass.vert", "shaders/wave.frag", true, new Resolution(), new Timer()),
	WORM("shaders/pass.vert", "shaders/worm.frag", true, new Resolution(), new Timer()),
	PLAYER_LIGHT("shaders/pass.vert", "shaders/darkness.frag", false, new Resolution(), new PlayerCoord(), new Light()),
	;
	
	private String vertId, fragId;
	private ShaderProgram shader;
	private ShaderStrategy[] strategies;
	private boolean background;
	
	Shader(String vertId, String fragId, Boolean background, ShaderStrategy... strategies) {
		this.vertId = vertId;
		this.fragId = fragId;
		this.strategies = strategies;
		this.background = background;
	}
	
	public void loadShader(PlayState state, String entityId, float duration) {
		
		if (this.equals(NOTHING)) {
			return;
		}
		
		shader = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		shader.begin();
		
		for (ShaderStrategy strat: strategies) {
			strat.create(state, shader);
		}
		
		shader.end();
		
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncShader(entityId, this, duration));
		}
	}
	
	public void shaderUpdate(PlayState state, float delta) {
		for (ShaderStrategy strat: strategies) {
			strat.controller(state, shader, delta);
		}
	}
	
	public void shaderResize(PlayState state) {
		for (ShaderStrategy strat: strategies) {
			strat.resize(state, shader);
		}
	}
	
	public ShaderProgram getShader() { return shader; }
	
	public boolean isBackground() { return background; }
}

