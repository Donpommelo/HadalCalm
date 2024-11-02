package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;

public class GomezsAmygdalaActivate extends SyncedAttacker {

    private static final float SPD_BUFF = 0.5f;
    private static final float DAMAGE_BUFF = 0.4f;
    private static final float BUFF_DURATION = 3.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC18_BUFF)
                .setVolume(0.5f)
                .setPosition(startPosition));

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.PICKUP_ENERGY, user)
                .setLifespan(BUFF_DURATION));

        user.getShaderHelper().setShader(Shader.PULSE_RED, BUFF_DURATION);

        user.getBodyData().addStatus(new StatusComposite(state, BUFF_DURATION, false, user.getBodyData(), user.getBodyData(),
                new StatChangeStatus(state, Stats.GROUND_SPD, SPD_BUFF, user.getBodyData()),
                new StatChangeStatus(state, Stats.DAMAGE_AMP, DAMAGE_BUFF, user.getBodyData())));
    }
}