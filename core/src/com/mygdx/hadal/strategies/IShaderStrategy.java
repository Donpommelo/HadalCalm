package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;

/**
 * This interface is used by shader strategies.
 * hbox strategies are attached to shaders and perform certain actions at specific times
 * @author Zachary Tu
 */
public interface IShaderStrategy {

	//this is run when the shader is created
	public void create(ShaderProgram shader);
	
	//this is run every engine tick when it is active
	public void playController(PlayState state, ShaderProgram shader, float delta);
	
	//this is run every engine tick when it is active in a non-play state (like as a background for the settings menu)
	public void defaultController(ShaderProgram shader, float delta);

	//this is called when the window is resized
	public void resize(ShaderProgram shader);
}
