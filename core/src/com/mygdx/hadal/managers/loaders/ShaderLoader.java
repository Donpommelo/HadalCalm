package com.mygdx.hadal.managers.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * RagdollLoader centralizes the loading of shaders.
 * This makes it easier for headless servers to skip this (since it doesn't have the shader files)
 */
public class ShaderLoader {

    /**
     * Load this shader's shader program if not created yet. Bind and initate the shader's strategies
     */
    public void loadShader(Shader shader) {
        if (shader.equals(Shader.NOTHING)) { return; }

        loadStaticShader(shader);

        shader.getShaderProgram().bind();
        for (ShaderStrategy strat : shader.getStrategies()) {
            strat.create(shader.getShaderProgram());
        }
    }

    /**
     * This loads a static shader
     */
    public void loadStaticShader(Shader shader) {
        if (shader.equals(Shader.NOTHING)) { return; }

        //load the shader and create its strategies
        if (null == shader.getShaderProgram()) {
            shader.setShaderProgram(new ShaderProgram(
                    Gdx.files.internal(shader.getVertId()).readString(),
                    Gdx.files.internal(shader.getFragId()).readString()));
        }
    }
}
