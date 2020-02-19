package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;

/**
 * A ShaderStrategy implements the methods used to affect a shader after it has been created
 * @author Zachary Tu
 */
public abstract class ShaderStrategy implements IShaderStrategy {

	@Override
	public void create(PlayState state, ShaderProgram shader) {}
	
	@Override
	public void controller(PlayState state, ShaderProgram shader, float delta) {}

	@Override
	public void resize(PlayState state, ShaderProgram shader) {}
}
