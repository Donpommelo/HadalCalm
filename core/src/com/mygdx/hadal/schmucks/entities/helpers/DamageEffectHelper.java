package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;

public class DamageEffectHelper {

    private static final float BASE_COOLDOWN = 1.0f;

    private final PlayState state;
    private final Schmuck schmuck;
    private float critCooldown, shieldCooldown;

    public DamageEffectHelper(PlayState state, Schmuck schmuck) {
        this.state = state;
        this.schmuck = schmuck;
    }


    public void controller(float delta) {

        if (critCooldown > 0.0f) {
            critCooldown -= delta;
        }

        if (shieldCooldown > 0.0f) {
            shieldCooldown -= delta;
        }
    }

    public void addCritFlash() {
        if (critCooldown <= 0.0f) {
            critCooldown = BASE_COOLDOWN;
            SoundEffect.SLASH.playSourced(state, schmuck.getPixelPosition(), 1.1f, 0.5f);

            ParticleEntity particle = new ParticleEntity(state, schmuck, Particle.EXPLOSION, 1.0f, 3.0f, true,
                    SyncType.NOSYNC);

            if (!state.isServer()) {
                ((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.EFFECT);
            }
        }
    }
}
