package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;

public class ArtifactAmmoActivate extends SyncedAttacker {

    private static final float DURATION = 1.5f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.RELOAD.playSourced(state, user.getPixelPosition(), 0.4f);
        ParticleEntity particle = new ParticleEntity(state, user, Particle.PICKUP_AMMO, 1.0f, DURATION, true,
                SyncType.NOSYNC);

        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.HBOX);
        }
    }
}