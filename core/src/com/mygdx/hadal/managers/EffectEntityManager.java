package com.mygdx.hadal.managers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class EffectEntityManager {


    public static ParticleEntity getParticle(PlayState state, ParticleCreate particleCreate) {
        if (null == HadalGame.assetManager) {
            if (particleCreate.getSyncType().equals(SyncType.CREATESYNC)) {
                if (particleCreate.getAttachedEntity() != null) {
                    PacketManager.serverUDPAll(new Packets.CreateParticles(particleCreate, true));
                } else {
                    PacketManager.serverUDPAll(new Packets.CreateParticles(particleCreate, false));
                }
            }
            return null;
        }

        ParticleEntity particleEntity = new ParticleEntity(state, particleCreate);
        modifyParticle(particleEntity, particleCreate);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(particleEntity.getEntityID(), particleEntity, false, ObjectLayer.EFFECT);
        }

        return particleEntity;
    }

    public static SoundEntity getSound(PlayState state, SoundCreate soundCreate) {
        if (null == HadalGame.assetManager) {
            if (soundCreate.getSyncType().equals(SyncType.CREATESYNC)) {
                if (null != soundCreate.getAttachedEntity()) {
                    PacketManager.serverUDPAll(new Packets.CreateSound(soundCreate));
                }
            }
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