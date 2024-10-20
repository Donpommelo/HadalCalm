package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class SpringLoaderUse extends SyncedAttacker {

    public static final float SPRING_DURATION = 6.0f;
    private static final Vector2 SPRING_RADIUS = new Vector2(96, 16);
    private static final float SPRING_POWER = 75.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.SPRING.playSourced(state, startPosition, 0.4f);
        Spring spring = new Spring(state,startPosition, SPRING_RADIUS, new Vector2(0, SPRING_POWER), SPRING_DURATION);
        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.MOMENTUM, startPosition)
                .setLifespan(1.0f));

        if (!state.isServer()) {
            ((ClientState) state).addEntity(spring.getEntityID(), spring, false, ObjectLayer.EFFECT);
        }
    }
}