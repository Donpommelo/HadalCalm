package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * A RandomSeed feeds the shader a random number upon creation. Used for random shaders like perlin noise
 * @author Dems Dremp
 */
public class RandomSeed extends ShaderStrategy {

	@Override
	public void create(ShaderProgram shader) {
		shader.setUniformf("u_random", MathUtils.random());
	}
}
