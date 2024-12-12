package com.mygdx.hadal.requests;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;

public class SoundLoad {

    private final SoundEffect sound;
    private float volume = 1.0f;
    private float pitch = 1.0f;
    private boolean singleton, noModifiers;

    private final Vector2 position = new Vector2();

    public SoundLoad(SoundEffect sound) {
        this.sound = sound;
    }

    public SoundLoad setVolume(float volume) {
        this.volume = volume;
        return this;
    }
    public SoundLoad setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SoundLoad setSingleton(boolean singleton) {
        this.singleton = singleton;
        return this;
    }

    public SoundLoad setNoModifiers(boolean noModifiers) {
        this.noModifiers = noModifiers;
        return this;
    }

    public SoundLoad setPosition(Vector2 position) {
        this.position.set(position);
        return this;
    }

    public SoundEffect getSound() { return sound; }

    public float getVolume() { return volume; }

    public float getPitch() { return pitch; }

    public boolean isSingleton() { return singleton; }

    public boolean isNoModifiers() { return noModifiers; }

    public Vector2 getPosition() { return position; }
}
