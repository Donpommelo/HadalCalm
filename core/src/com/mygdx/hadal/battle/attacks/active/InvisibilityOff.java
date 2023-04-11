package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class InvisibilityOff extends SyncedAttacker {

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {

        ParticleEntity particle = new ParticleEntity(state, user, Particle.SMOKE, 1.0f, 3.0f, true,
                SyncType.NOSYNC).setScale(0.4f);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.EFFECT);
        }

        if (user instanceof Player player) {
            player.getEffectHelper().setInvisible(false);
        }
    }
}