package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;

/**
 * A ShaderStrategy implements the methods used to affect a shader after it has been created
 * @author Bendiana Boguana
 */
public abstract class ShaderStrategy implements IShaderStrategy {

	@Override
	public void create(ShaderProgram shader) {}
	
	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {}
	
	@Override
	public void defaultController(ShaderProgram shader, float delta) {}

	@Override
	public void shaderEntityUpdate(ShaderProgram shader, float completion) {}

	@Override
	public void resize(ShaderProgram shader) {}
}
