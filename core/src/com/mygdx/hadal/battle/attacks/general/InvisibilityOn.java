package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;

public class InvisibilityOn extends SyncedAttacker {

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC27_EVIL)
                .setPosition(startPosition));

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE, user)
                .setLifespan(1.0f)
                .setScale(0.4f));

        float duration = 0.0f;
        if (extraFields.length > 0) {
            duration = extraFields[0];
        }

        user.getBodyData().addStatus(new Invisibility(state, duration, user.getBodyData(), user.getBodyData()));
    }
}