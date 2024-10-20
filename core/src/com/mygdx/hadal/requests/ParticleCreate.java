package com.mygdx.hadal.requests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.HadalEntity;

public class ParticleCreate {

    private final Particle particle;

    private HadalEntity attachedEntity;
    private final Vector2 position = new Vector2();
    private float linger = 1.0f;
    private float lifespan = 0.0f;
    private boolean startOn = true;
    private SyncType syncType = SyncType.NOSYNC;

    private boolean rotate, showOnInvis;
    private Float scale, angle, velocity, prematureOff;
    private HadalColor color = HadalColor.NOTHING;
    private final Vector3 colorRGB = new Vector3();

    public ParticleCreate(Particle particle, HadalEntity attachedEntity) {
        this.particle = particle;
        this.attachedEntity = attachedEntity;
    }

    public ParticleCreate(Particle particle, Vector2 position) {
        this.particle = particle;
        this.position.set(position);
    }

    public ParticleCreate setLinger(float linger) {
        this.linger = linger;
        return this;
    }

    public ParticleCreate setLifespan(float lifespan) {
        this.lifespan = lifespan;
        return this;
    }

    public ParticleCreate setStartOn(boolean startOn) {
        this.startOn = startOn;
        return this;
    }

    public ParticleCreate setSyncType(SyncType syncType) {
        this.syncType = syncType;
        return this;
    }

    public ParticleCreate setRotate(boolean rotate) {
        this.rotate = rotate;
        return this;
    }

    public ParticleCreate setShowOnInvis(boolean showOnInvis) {
        this.showOnInvis = showOnInvis;
        return this;
    }

    public ParticleCreate setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public ParticleCreate setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public ParticleCreate setVelocity(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public ParticleCreate setPrematureOff(float prematureOff) {
        this.prematureOff = prematureOff;
        return this;
    }

    public ParticleCreate setColor(HadalColor color) {
        this.color = color;
        return this;
    }

    public ParticleCreate setColor(Vector3 colorRGB) {
        this.colorRGB.set(colorRGB);
        return this;
    }

    public Particle getParticle() { return particle; }

    public HadalEntity getAttachedEntity() { return attachedEntity; }

    public Vector2 getPosition() { return position; }

    public float getLinger() { return linger; }

    public float getLifespan() { return lifespan; }

    public boolean isStartOn() { return startOn; }

    public SyncType getSyncType() { return syncType; }

    public boolean isRotate() { return rotate; }

    public boolean isShowOnInvis() { return showOnInvis; }

    public Float getScale() { return scale; }

    public Float getAngle() { return angle; }

    public Float getVelocity() { return velocity; }

    public Float getPrematureOff() { return prematureOff; }

    public HadalColor getColor() { return color; }

    public Vector3 getColorRGB() { return colorRGB; }
}