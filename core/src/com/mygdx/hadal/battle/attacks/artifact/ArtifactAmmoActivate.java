package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class ArtifactAmmoActivate extends SyncedAttacker {

    private static final float DURATION = 1.5f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.RELOAD.playSourced(state, user.getPixelPosition(), 0.4f);
        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.PICKUP_AMMO, user)
                .setLifespan(DURATION));
    }
}