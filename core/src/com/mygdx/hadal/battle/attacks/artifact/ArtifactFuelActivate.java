package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class ArtifactFuelActivate extends SyncedAttacker {

    private static final float DURATION = 1.5f;

    private final UnlockArtifact effectSource;

    public ArtifactFuelActivate(UnlockArtifact effectSource) {
        this.effectSource = effectSource;
    }

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC2_FUEL)
                .setVolume(0.4f)
                .setPosition(startPosition));

        ((Player) user).getArtifactIconHelper().addArtifactFlash(effectSource);

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.PICKUP_ENERGY, user)
                .setLifespan(DURATION));
    }
}