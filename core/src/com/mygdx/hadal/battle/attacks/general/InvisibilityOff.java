package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class InvisibilityOff extends SyncedAttacker {

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE, user)
                .setLifespan(1.0f)
                .setScale(0.4f));

        if (user instanceof Player player) {
            player.getEffectHelper().setInvisible(false);
        }
    }
}