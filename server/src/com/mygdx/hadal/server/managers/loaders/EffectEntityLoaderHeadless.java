package com.mygdx.hadal.server.managers.loaders;

import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.loaders.EffectEntityLoader;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

public class EffectEntityLoaderHeadless extends EffectEntityLoader {

    @Override
    public ParticleEntity getParticle(PlayState state, ParticleCreate particleCreate) {
        if (particleCreate.getSyncType().equals(SyncType.CREATESYNC)) {
            if (particleCreate.getScale() == null) {
                particleCreate.setScale(1.0f);
            }
            if (particleCreate.getVelocity() == null) {
                particleCreate.setVelocity(0.0f);
            }
            PacketManager.serverUDPAll(new Packets.CreateParticles(particleCreate));
        }
        return null;
    }

    @Override
    public SoundEntity getSound(PlayState state, SoundCreate soundCreate) {
        if (soundCreate.getSyncType().equals(SyncType.CREATESYNC)) {
            if (null != soundCreate.getAttachedEntity()) {
                PacketManager.serverUDPAll(new Packets.CreateSound(soundCreate));
            }
        }
        return null;
    }
}
