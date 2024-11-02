package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
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
            SoundManager.play(state, new SoundLoad(SoundEffect.SLASH)
                    .setVolume(1.1f)
                    .setPitch(0.5f)
                    .setPosition(schmuck.getPixelPosition()));

            EffectEntityManager.getParticle(state, new ParticleCreate(Particle.EXPLOSION, schmuck)
                    .setLifespan(1.0f));
        }
    }
}
