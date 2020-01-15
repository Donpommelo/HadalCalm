package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * @author Zachary Tu
 *
 */
public class Resolution extends ShaderStrategy {

	@Override
	public void create(PlayState state, ShaderProgram shader) {
		shader.setUniformf("u_resolution", HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
	}

	@Override
	public void resize(PlayState state, ShaderProgram shader, int width, int height) {
		shader.setUniformf("u_resolution", width, height);
	}
}
