package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * A RandomSeed feeds the shader a random number upon creation. Used for random shaders like perlin noise
 * @author Dems Dremp
 */
public class SetVariable extends ShaderStrategy {

	private final String name;
	private final float input;
	public SetVariable(String name, float input) {
		this.name = name;
		this.input = input;
	}

	@Override
	public void create(ShaderProgram shader) {
		shader.setUniformf(name, input);
	}
}
