package com.mygdx.hadal.managers;

import com.mygdx.hadal.managers.loaders.EffectEntityLoader;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.PlayState;

/**
 * EffectEntityManager creates particle and sound entities.
 * Logic is delegated to Loader to make it easier for headless server to have different logic
 */
public class EffectEntityManager {

    private static EffectEntityLoader loader;

    public static void initLoader(EffectEntityLoader loader) {
        EffectEntityManager.loader = loader;
    }

    public static ParticleEntity getParticle(PlayState state, ParticleCreate particleCreate) {
        return EffectEntityManager.loader.getParticle(state, particleCreate);
    }

    public static SoundEntity getSound(PlayState state, SoundCreate soundCreate) {
        return EffectEntityManager.loader.getSound(state, soundCreate);
    }
}