package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;

/**
 * This interface is used by shader strategies.
 * hbox strategies are attached to shaders and perform certain actions at specific times
 * @author Zachary Tu
 *
 */
public interface IShaderStrategy {

	public void create(ShaderProgram shader);
	
	public void playController(PlayState state, ShaderProgram shader, float delta);
	
	public void defaultController(ShaderProgram shader, float delta);

	public void resize(ShaderProgram shader);
}
