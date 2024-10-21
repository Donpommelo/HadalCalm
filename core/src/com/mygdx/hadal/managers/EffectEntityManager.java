package com.mygdx.hadal.managers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class EffectEntityManager {


    public static ParticleEntity getParticle(PlayState state, ParticleCreate particleCreate) {
        if (null == HadalGame.assetManager) {
            return null;
        }

        ParticleEntity particleEntity = new ParticleEntity(state, particleCreate);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(particleEntity.getEntityID(), particleEntity, false, ObjectLayer.EFFECT);
        }

        modifyParticle(particleEntity, particleCreate);

        return particleEntity;
    }

    public static SoundEntity getSound(PlayState state, SoundCreate soundCreate) {
        if (null == HadalGame.assetManager) {
            return null;
        }

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