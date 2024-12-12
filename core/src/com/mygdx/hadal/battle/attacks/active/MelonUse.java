package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Regeneration;

public class MelonUse extends SyncedAttacker {

    public static final float DURATION = 8.0f;
    public static final float POWER = 0.04f;

    private static final float PARTICLE_DURATION = 1.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.EATING)
                .setVolume(0.8f)
                .setPosition(startPosition));

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.KAMABOKO_IMPACT, user)
                .setLifespan(PARTICLE_DURATION)
                .setColor(HadalColor.FRENCH_LIME));

        user.getBodyData().addStatus(new Regeneration(state, DURATION, user.getBodyData(), user.getBodyData(),
                POWER * user.getBodyData().getStat(Stats.MAX_HP)));
    }
}