package com.mygdx.hadal.managers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.requests.ParticleRequest;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class EffectEntityManager {


    public static ParticleEntity getParticle(PlayState state, HadalEntity entity, ParticleRequest particleRequest) {
        if (null == HadalGame.assetManager) { return null; }

        ParticleEntity particleEntity = new ParticleEntity(state, entity, particleRequest);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(particleEntity.getEntityID(), particleEntity, particleRequest.isSync(), ObjectLayer.EFFECT);
        }
        return particleEntity;
    }
}
