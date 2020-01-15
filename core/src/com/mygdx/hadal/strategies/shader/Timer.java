package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * @author Zachary Tu
 *
 */
public class Timer extends ShaderStrategy {

	@Override
	public void controller(PlayState state, ShaderProgram shader, float delta) {
		shader.setUniformf("u_time", delta);
	}
}
