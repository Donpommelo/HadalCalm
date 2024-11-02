package com.mygdx.hadal.requests;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;

public class RagdollCreate {

    //these control the ragdoll fading before despawning
    private static final float FADE_LIFESPAN = 1.0f;

    private Sprite sprite = Sprite.NOTHING;
    private TextureRegion textureRegion;
    private Shader fadeShader = Shader.NOTHING;
    private final Vector2 position = new Vector2();
    private final Vector2 size = new Vector2();
    private final Vector2 velocity = new Vector2();

    private float lifespan;
    private float gravity;
    private float fadeDuration;
    private float angularDampening, linearDampening;

    private boolean startVelocity, sensor, fade;
    private boolean spinning = true;

    public RagdollCreate setFade() {
        return setFade(FADE_LIFESPAN, Shader.FADE);
    }

    public RagdollCreate setFade(float fadeDuration, Shader fadeShader) {
        this.fade = true;
        this.fadeDuration = fadeDuration;
        this.fadeShader = fadeShader;
        return this;
    }

    public RagdollCreate setSprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public RagdollCreate setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        return this;
    }

    public RagdollCreate setPosition(Vector2 position) {
        this.position.set(position);
        return this;
    }

    public RagdollCreate setSize(Vector2 size) {
        this.size.set(size);
        return this;
    }

    public RagdollCreate setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
        return this;
    }

    public RagdollCreate setLifespan(float lifespan) {
        this.lifespan = lifespan;
        return this;
    }

    public RagdollCreate setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    public RagdollCreate setDampening(float angularDampening, float linearDampening) {
        this.angularDampening = angularDampening;
        this.linearDampening = linearDampening;
        return this;
    }

    public RagdollCreate setStartVelocity(boolean startVelocity) {
        this.startVelocity = startVelocity;
        return this;
    }

    public RagdollCreate setSensor(boolean sensor) {
        this.sensor = sensor;
        return this;
    }

    public RagdollCreate setSpinning(boolean spinning) {
        this.spinning = spinning;
        return this;
    }

    public Sprite getSprite() { return sprite; }

    public TextureRegion getTextureRegion() { return textureRegion; }

    public Shader getFadeShader() { return fadeShader; }

    public Vector2 getPosition() { return position; }

    public Vector2 getSize() { return size; }

    public Vector2 getVelocity() { return velocity; }

    public float getLifespan() { return lifespan; }

    public float getGravity() { return gravity; }

    public float getFadeDuration() { return fadeDuration; }

    public float getAngularDampening() { return angularDampening; }

    public float getLinearDampening() { return linearDampening; }

    public boolean isStartVelocity() { return startVelocity; }

    public boolean isSensor() { return sensor; }

    public boolean isFade() { return fade; }

    public boolean isSpinning() { return spinning; }
}
