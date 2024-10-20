package com.mygdx.hadal.requests;

import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;

public class ParticleRequest {
    private final Particle particle;
    private float linger = 1.0f;
    private float lifespan = 0.0f;
    private boolean startOn = true;
    private boolean sync = false;
    private SyncType syncType = SyncType.NOSYNC;

    public ParticleRequest(Particle particle) {
        this.particle = particle;
    }

    public ParticleRequest setLinger(float linger) {
        this.linger = linger;
        return this;
    }

    public ParticleRequest setLifespan(float lifespan) {
        this.lifespan = lifespan;
        return this;
    }

    public ParticleRequest setStartOn(boolean startOn) {
        this.startOn = startOn;
        return this;
    }

    public ParticleRequest setSync(boolean sync) {
        this.sync = sync;
        return this;
    }

    public ParticleRequest setSyncType(SyncType syncType) {
        this.syncType = syncType;
        return this;
    }

    public Particle getParticle() { return particle; }

    public float getLinger() { return linger; }

    public float getLifespan() { return lifespan; }

    public boolean isStartOn() { return startOn; }

    public boolean isSync() { return sync; }

    public SyncType getSyncType() { return syncType; }
}