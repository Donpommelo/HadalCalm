package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.strategies.shader.*;

/**
 * A Shader represents a vertex and fragment shader shader.
 * @author Krarbcaster Korgette
 */
public enum Shader {

	NOTHING("", "", true),
	BLACKWHITE("pass", "blackwhite", true),
	CENSURE("pass", "censure", true),
	EMBOSS("pass", "emboss", true, new Resolution()),
	FADE("pass", "fade", true, new Completion(), new Timer()),
	INVISIBLE("pass", "pass", true),
	GREYSCALE("pass", "greyscale", true),
	OUTLINE("pass", "outline", true),
	SEPIA("pass", "sepia", true),
	STATIC("pass", "static", true, new Timer()),
	WATER("pass", "water", true, new Timer()),
	PULSE_RED("pass", "pulsered", true, new Timer(), new SetVariable("speed", 10)),
	PULSE_RED_HP("pass", "pulsered", true, new Timer(), new PlayerHpScale()),
	PULSE_WHITE("pass", "pulsewhite", true, new Timer()),
	WHITE("pass", "white", true),
	INVERT("pass", "invert", true),
	CLOUD("pass", "cloud", true, new Resolution(), new Timer(), new CameraCoord()),
	SPLASH("pass", "splash", true, new Resolution(), new Timer()),
	DRIP("pass", "drip", true, new Resolution(), new Timer()),
	NORTHERN_LIGHTS("pass", "northern_lights", true, new Resolution(), new Timer()),
	PLASMA("pass", "plasma", true, new Resolution(), new Timer()),
	WAVE("pass", "wave", true, new Resolution(), new Timer()),
	WIGGLE_STATIC("pass", "wigglestatic", true, new Resolution(), new Timer()),
	WORM("pass", "worm", true, new Resolution(), new Timer()),
	WHIRLPOOL("pass", "whirlpool", true, new Resolution(), new Timer(), new ObjectiveCoord()),
	PLAYER_LIGHT("pass", "darkness", false, new Resolution(), new PlayerCoord(), new Light()),
	PERLIN_FADE("pass", "perlin", false, new Resolution(), new Timer(), new RandomSeed()),
	PERLIN_COLOR_FADE("pass", "perlin_color", false, new Resolution(), new Timer(), new RandomSeed()),
	;
	
	//filename for the vertex and fragment shaders
	private final String vertId, fragId;
	
	//the shader program. This is the thing actually used to draw the shader
	private ShaderProgram shaderProgram;
	
	//a list of strategies the shader can use to read game information.
	private final ShaderStrategy[] strategies;
	
	//is this shader displayed in the background or foreground?
	private final boolean background;
	
	Shader(String vertId, String fragId, Boolean background, ShaderStrategy... strategies) {
		this.vertId = getVertFileName(vertId);
		this.fragId = getFragFileName(fragId);
		this.strategies = strategies;
		this.background = background;
	}
	
	/**
	 * Load this shader's shader program if not created yet. Bind and initate the shader's strategies
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

		for (ShaderStrategy strat : strategies) {
			strat.create(shaderProgram);
		}
	}

	/**
	 * This is called when a play state is initiated
	 * It disposes of shaders to free up memory
	 */
	public static void clearShader() {
		for (Shader shader : Shader.values()) {
			if (shader.shaderProgram != null) {
				shader.shaderProgram.dispose();
				shader.shaderProgram = null;
			}
		}
	}
	
	/**
	 * This is run every game update and defers to the shader strategies to process game information
	 */
	public void shaderPlayUpdate(PlayState state, float delta) {
		for (ShaderStrategy strat : strategies) {
			strat.playController(state, shaderProgram, delta);
		}
	}
	
	/**
	 * This is a version of shaderPlayUpdate used for backgrounds of non-playstate states
	 */
	public void shaderDefaultUpdate(float delta) {
		for (ShaderStrategy strat : strategies) {
			strat.defaultController(shaderProgram, delta);
		}
	}

	/**
	 * This is a version of shaderPlayUpdate used for entities with temporary shader effects.
	 * Use this for shaders that keep track of percent completion
	 */
	public void shaderEntityUpdate(HadalEntity entity, float completion) {
		for (ShaderStrategy strat : strategies) {
			strat.shaderEntityUpdate(shaderProgram, entity, completion);
		}
	}
	
	/**
	 * This is run when the game window is resized and defers to the shader strategies to process game information
	 */
	public void shaderResize() {
		for (ShaderStrategy strat : strategies) {
			strat.resize(shaderProgram);
		}
	}

	private String getVertFileName(String filename) {
		return "shaders/" + filename + ".vert";
	}

	private String getFragFileName(String filename) {
		return "shaders/" + filename + ".frag";
	}

	public ShaderProgram getShaderProgram() { return shaderProgram; }
	
	public boolean isBackground() { return background; }
}
