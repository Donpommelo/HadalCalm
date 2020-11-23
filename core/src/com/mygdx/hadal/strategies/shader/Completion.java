package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * A Timer feeds the shader the game time. Used by animated shaders that change over time.
 * @author Grinniva Glortrand
 */
public class Completion extends ShaderStrategy {

	@Override
	public void shaderEntityUpdate(ShaderProgram shader, float completion) {
		shader.setUniformf("completion", completion);
	}

	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		shader.setUniformf("u_time", delta);
	}
}
