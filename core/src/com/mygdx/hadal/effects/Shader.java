package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.strategies.shader.*;

/**
 * A Shader represents a vertex and fragment shader shader.
 * @author Krarbcaster Korgette
 */
public enum Shader {

	NOTHING("", "", true),
	BLACKWHITE("shaders/pass.vert", "shaders/blackwhite.frag", true),
	CENSURE("shaders/pass.vert", "shaders/censure.frag", true),
	FADE("shaders/pass.vert", "shaders/fade.frag", true, new Completion(), new Timer()),
	INVISIBLE("shaders/pass.vert", "shaders/pass.frag", true),
	GREYSCALE("shaders/pass.vert", "shaders/greyscale.frag", true),
	OUTLINE("shaders/pass.vert", "shaders/outline.frag", true),
	SEPIA("shaders/pass.vert", "shaders/sepia.frag", true),
	WHITE("shaders/pass.vert", "shaders/white.frag", true),
	INVERT("shaders/pass.vert", "shaders/invert.frag", true),
	CLOUD("shaders/pass.vert", "shaders/cloud.frag", true, new Resolution(), new Timer(), new CameraCoord()),
	SPLASH("shaders/pass.vert", "shaders/splash.frag", true, new Resolution(), new Timer()),
	DRIP("shaders/pass.vert", "shaders/drip.frag", true, new Resolution(), new Timer()),
	NORTHERN_LIGHTS("shaders/pass.vert", "shaders/northern_lights.frag", true, new Resolution(), new Timer()),
	PLASMA("shaders/pass.vert", "shaders/plasma.frag", true, new Resolution(), new Timer()),
	WAVE("shaders/pass.vert", "shaders/wave.frag", true, new Resolution(), new Timer()),
	WIGGLE_STATIC("shaders/pass.vert", "shaders/wigglestatic.frag", true, new Resolution(), new Timer()),
	WORM("shaders/pass.vert", "shaders/worm.frag", true, new Resolution(), new Timer()),
	WHIRLPOOL("shaders/pass.vert", "shaders/whirlpool.frag", true, new Resolution(), new Timer(), new ObjectiveCoord()),
	PLAYER_LIGHT("shaders/pass.vert", "shaders/darkness.frag", false, new Resolution(), new PlayerCoord(), new Light()),
	;
	
	//filename for the vertex and fragment shaders
	private final String vertId, fragId;
	
	//the shader program.
	private ShaderProgram shaderProgram;
	
	//a list of strategies the shader can use to read game information.
	private final ShaderStrategy[] strategies;
	
	//is this shader displayed in the background or foreground?
	private final boolean background;
	
	Shader(String vertId, String fragId, Boolean background, ShaderStrategy... strategies) {
		this.vertId = vertId;
		this.fragId = fragId;
		this.strategies = strategies;
		this.background = background;
	}
	
	/**
	 * Load this shader's shader program
	 */
	public void loadShader() {
		
		if (this.equals(NOTHING)) {
			return;
		}
		
		//load the shader and create its strategies
		if (shaderProgram == null) {
			shaderProgram = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		}
		shaderProgram.bind();

		for (ShaderStrategy strat: strategies) {
			strat.create(shaderProgram);
		}
	}
	
	/**
	 * This is run every game update and defers to the shader strategies to process game information
	 */
	public void shaderPlayUpdate(PlayState state, float delta) {
		for (ShaderStrategy strat: strategies) {
			strat.playController(state, shaderProgram, delta);
		}
	}
	
	/**
	 * This is a version of shaderPlayUpdate used for backgrounds of non-playstate states
	 */
	public void shaderDefaultUpdate(float delta) {
		for (ShaderStrategy strat: strategies) {
			strat.defaultController(shaderProgram, delta);
		}
	}

	/**
	 * This is a version of shaderPlayUpdate used for entities with temporary shader effects.
	 * Use this for shaders that keep track of percent completion
	 */
	public void shaderEntityUpdate(float completion) {
		for (ShaderStrategy strat: strategies) {
			strat.shaderEntityUpdate(shaderProgram, completion);
		}
	}
	
	/**
	 * This is run when the game window is resized and defers to the shader strategies to process game information
	 */
	public void shaderResize() {
		for (ShaderStrategy strat: strategies) {
			strat.resize(shaderProgram);
		}
	}
	
	public ShaderProgram getShaderProgram() { return shaderProgram; }
	
	public boolean isBackground() { return background; }
}
