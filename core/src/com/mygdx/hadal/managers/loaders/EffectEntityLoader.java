package com.mygdx.hadal.managers.loaders;

import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class EffectEntityLoader {

    public ParticleEntity getParticle(PlayState state, ParticleCreate particleCreate) {
        ParticleEntity particleEntity = new ParticleEntity(state, particleCreate);
        modifyParticle(particleEntity, particleCreate);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(particleEntity.getEntityID(), particleEntity, false, ObjectLayer.EFFECT);
        }

        return particleEntity;
    }

    public SoundEntity getSound(PlayState state, SoundCreate soundCreate) {
        SoundEntity soundEntity = new SoundEntity(state, soundCreate);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(soundEntity.getEntityID(), soundEntity, false, ObjectLayer.EFFECT);
        }

        return soundEntity;
    }

    public static void modifyParticle(ParticleEntity particleEntity, ParticleCreate particleCreate) {
        particleEntity.setRotate(particleCreate.isRotate());
        particleEntity.setShowOnInvis(particleCreate.isShowOnInvis());
        particleEntity.setColor(particleCreate.getColor());

        if (!particleCreate.getColorRGB().isZero()) {
            particleEntity.setColor(particleCreate.getColorRGB());
        }
        if (!particleCreate.getOffset().isZero()) {
            particleEntity.setOffset(particleCreate.getOffset().x, particleCreate.getOffset().y);
        }
        if (particleCreate.getScale() != null) {
            particleEntity.setScale(particleCreate.getScale());
        }
        if (particleCreate.getAngle() != null) {
            particleEntity.setParticleAngle(particleCreate.getAngle());
        }
        if (particleCreate.getVelocity() != null) {
            particleEntity.setParticleVelocity(particleCreate.getVelocity());
        }
    }
}