package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * A Resolution sets the shader's resolution when it is created.
 * It also updates the shader's resolution when the game window is resized.
 * @author Weezlebub Whekins
 *
 */
public class Resolution extends ShaderStrategy {

	@Override
	public void create(ShaderProgram shader) {
		shader.setUniformf("u_resolution", HadalGame.viewportCamera.getScreenWidth(), HadalGame.viewportCamera.getScreenHeight());
	}

	@Override
	public void resize(ShaderProgram shader) {
		shader.setUniformf("u_resolution", HadalGame.viewportCamera.getScreenWidth(), HadalGame.viewportCamera.getScreenHeight());
	}
}
