package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;

public class InvisibilityOn extends SyncedAttacker {

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC27_EVIL.playSourced(state, user.getPixelPosition(), 1.0f);

        float duration = 0.0f;
        if (extraFields.length > 0) {
            duration = extraFields[0];
        }

        ParticleEntity particle = new ParticleEntity(state, user, Particle.SMOKE, 1.0f, 3.0f, true,
                SyncType.NOSYNC).setScale(0.4f);

        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.EFFECT);
        }

        user.getBodyData().addStatus(new Invisibility(state, duration, user.getBodyData(), user.getBodyData()));
    }
}