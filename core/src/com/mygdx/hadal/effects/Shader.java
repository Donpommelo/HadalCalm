package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.strategies.shader.*;

/**
 * A Shader represents a vertex and fragment shader shader.
 * @author Zachary Tu
 */
public enum Shader {

	NOTHING("", "", true),
	CENSURE("shaders/pass.vert", "shaders/censure.frag", true),
	INVISIBLE("shaders/pass.vert", "shaders/pass.frag", true),
	SEPIA("shaders/pass.vert", "shaders/sepia.frag", true),
	WHITE("shaders/pass.vert", "shaders/white.frag", true),
	INVERT("shaders/pass.vert", "shaders/invert.frag", true),
	SPLASH("shaders/pass.vert", "shaders/splash.frag", true, new Resolution(), new Timer()),
	DRIP("shaders/pass.vert", "shaders/drip.frag", true, new Resolution(), new Timer()),
	WAVE("shaders/pass.vert", "shaders/wave.frag", true, new Resolution(), new Timer()),
	WORM("shaders/pass.vert", "shaders/worm.frag", true, new Resolution(), new Timer()),
	WHIRLPOOL("shaders/pass.vert", "shaders/whirlpool.frag", true, new Resolution(), new Timer(), new ObjectiveCoord()),
	PLAYER_LIGHT("shaders/pass.vert", "shaders/darkness.frag", false, new Resolution(), new PlayerCoord(), new Light()),
	;
	
	//filename for the vertex and fragment shaders
	private final String vertId, fragId;
	
	//the shader program.
	private ShaderProgram shader;
	
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
	 * 
	 * @param state: The game state
	 * @param entityId: The id of the entity that will be shaded
	 * @param duration: how long will this shader last?
	 */
	public void loadShader(PlayState state, String entityId, float duration) {
		
		if (this.equals(NOTHING)) {
			return;
		}
		
		//load the shader and create its strategies
		shader = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		shader.bind();

		for (ShaderStrategy strat: strategies) {
			strat.create(shader);
		}
		
		//The server tells the client to also display the shader
		if (state.isServer()) {
			HadalGame.server.sendToAllUDP(new Packets.SyncShader(entityId, this, duration));
		}
	}
	
	/**
	 * This is a version of loadShader used for backgrounds of non-playstate states.
	 */
	public void loadDefaultShader() {
		if (this.equals(NOTHING)) {
			return;
		}
		
		//load the shader and create its strategies
		shader = new ShaderProgram(Gdx.files.internal(vertId).readString(), Gdx.files.internal(fragId).readString());
		shader.bind();
		
		for (ShaderStrategy strat: strategies) {
			strat.create(shader);
		}
	}
	
	/**
	 * This is run every game update and defers to the shader strategies to process game information
	 */
	public void shaderPlayUpdate(PlayState state, float delta) {
		for (ShaderStrategy strat: strategies) {
			strat.playController(state, shader, delta);
		}
	}
	
	/**
	 * This is a version of shaderPlayUpdate used for backgrounds of non-playstate states
	 */
	public void shaderDefaultUpdate(float delta) {
		for (ShaderStrategy strat: strategies) {
			strat.defaultController(shader, delta);
		}
	}
	
	/**
	 * This is run when the game window is resized and defers to the shader strategies to process game information
	 */
	public void shaderResize() {
		for (ShaderStrategy strat: strategies) {
			strat.resize(shader);
		}
	}
	
	public ShaderProgram getShader() { return shader; }
	
	public boolean isBackground() { return background; }
}
