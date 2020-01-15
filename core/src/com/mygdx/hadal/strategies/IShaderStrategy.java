package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Zachary Tu
 *
 */
public interface IShaderStrategy {

	public void create(PlayState state, ShaderProgram shader);
	
	public void controller(PlayState state, ShaderProgram shader, float delta);
	
	public void resize(PlayState state, ShaderProgram shader, int width, int height);
}
