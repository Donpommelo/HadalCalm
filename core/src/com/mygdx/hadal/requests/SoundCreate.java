package com.mygdx.hadal.requests;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;

public class SoundCreate {

    private final SoundEffect sound;
    private final HadalEntity attachedEntity;
    private float lifespan = 0.0f;
    private float volume = 1.0f;
    private float pitch = 1.0f;

    private boolean startOn = true, looped = true;
    private SyncType syncType = SyncType.NOSYNC;

    public SoundCreate(SoundEffect sound, HadalEntity attachedEntity) {
        this.sound = sound;
        this.attachedEntity = attachedEntity;
    }

    public SoundCreate setLifespan(float lifespan) {
        this.lifespan = lifespan;
        return this;
    }

    public SoundCreate setVolume(float volume) {
        this.volume = volume;
        return this;
    }
    public SoundCreate setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SoundCreate setStartOn(boolean startOn) {
        this.startOn = startOn;
        return this;
    }

    public SoundCreate setLooped(boolean looped) {
        this.looped = looped;
        return this;
    }

    public SoundCreate setSyncType(SyncType syncType) {
        this.syncType = syncType;
        return this;
    }

    public SoundEffect getSound() { return sound; }

    public HadalEntity getAttachedEntity() { return attachedEntity; }

    public float getLifespan() { return lifespan; }

    public float getVolume() { return volume; }

    public float getPitch() { return pitch; }

    public boolean isStartOn() { return startOn; }

    public boolean isLooped() { return looped; }

    public SyncType getSyncType() { return syncType; }
}
