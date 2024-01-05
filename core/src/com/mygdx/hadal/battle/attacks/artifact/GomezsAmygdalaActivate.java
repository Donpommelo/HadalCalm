package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;

public class GomezsAmygdalaActivate extends SyncedAttacker {

    private static final float spdBuff = 0.5f;
    private static final float damageBuff = 0.4f;
    private static final float buffDuration = 3.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC18_BUFF.playSourced(state, user.getPixelPosition(), 0.5f);

        ParticleEntity particle = new ParticleEntity(state, user, Particle.PICKUP_ENERGY, 1.0f, buffDuration, true,
                SyncType.NOSYNC);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.HBOX);
        }

        user.getShaderHelper().setShader(Shader.PULSE_RED, buffDuration);

        user.getBodyData().addStatus(new StatusComposite(state, buffDuration, false, user.getBodyData(), user.getBodyData(),
                new StatChangeStatus(state, Stats.GROUND_SPD, spdBuff, user.getBodyData()),
                new StatChangeStatus(state, Stats.DAMAGE_AMP, damageBuff, user.getBodyData())));
    }
}