package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Regeneration;

public class MelonUse extends SyncedAttacker {

    public static final float DURATION = 8.0f;
    public static final float POWER = 0.04f;

    private static final float PARTICLE_DURATION = 1.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.EATING.playSourced(state, user.getPixelPosition(), 0.8f);
        ParticleEntity particle = new ParticleEntity(state, user, Particle.KAMABOKO_IMPACT, 0.0f, PARTICLE_DURATION,
                true, SyncType.NOSYNC).setColor(HadalColor.FRENCH_LIME);

        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.EFFECT);
        }

        user.getBodyData().addStatus(new Regeneration(state, DURATION, user.getBodyData(), user.getBodyData(),
                POWER * user.getBodyData().getStat(Stats.MAX_HP)));
    }
}