package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class FracturePlateActivate extends SyncedAttacker {

    private static final float DURATION = 1.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {

        ParticleEntity particle = new ParticleEntity(state, user, Particle.SHIELD, 1.0f, DURATION,true, SyncType.NOSYNC);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, ObjectLayer.HBOX);
        }
    }
}