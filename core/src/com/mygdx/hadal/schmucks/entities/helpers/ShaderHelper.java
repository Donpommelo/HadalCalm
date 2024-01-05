package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.states.PlayState;

public class ShaderHelper {

    private final PlayState state;
    private final HadalEntity entity;

    //Keeps track of an entity's shader such as when flashing after receiving damage.
    //Static shaders do not need to be updated with variables.
    private float shaderDuration;
    private float shaderCount, shaderStaticCount;
    private Shader shader, shaderStatic;

    public ShaderHelper(PlayState state, HadalEntity entity) {
        this.state = state;
        this.entity = entity;
    }

    /**
     * Set this entity's shader (this will be used when rendering this entity)
     * @param shader: shader to use
     * @param shaderCount: how long does this shader last?
     */
    public void setShader(Shader shader, float shaderCount) {
        shader.loadShader();
        this.shader = shader;
        this.shaderDuration = shaderCount;
        this.shaderCount = shaderCount;
    }

    public void setStaticShader(Shader shader, float shaderCount) {
        shader.loadStaticShader();
        this.shaderStatic = shader;
        this.shaderStaticCount = shaderCount;
    }

    public void decreaseShaderCount(float i) {
        shaderCount -= i;
        shaderStaticCount -= i;
    }

    /**
     * This is run when the entity is rendered.
     * Give the shader information about its duration
     */
    public void processShaderController(float timer) {
        float percentageCompletion = MathUtils.clamp(1.0f - shaderCount / shaderDuration, 0, 1.0f);
        shader.shaderPlayUpdate(state, timer);
        shader.shaderDefaultUpdate(timer);
        shader.shaderEntityUpdate(entity, percentageCompletion);
    }

    public Shader getShader() { return shader; }

    public float getShaderCount() { return shaderCount; }

    public Shader getShaderStatic() { return shaderStatic; }

    public float getShaderStaticCount() { return shaderStaticCount; }
}
