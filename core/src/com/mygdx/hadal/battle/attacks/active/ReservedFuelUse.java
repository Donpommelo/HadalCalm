package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

public class ReservedFuelUse extends SyncedAttacker {

    public static final float DURATION = 5.0f;
    public static final float POWER = 18.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC2_FUEL)
                .setVolume(0.5f)
                .setPosition(startPosition));

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BRIGHT, user)
                .setLifespan(DURATION)
                .setColor(HadalColor.FRENCH_LIME));

        user.getBodyData().addStatus(new StatChangeStatus(state, DURATION, Stats.FUEL_REGEN, POWER, user.getBodyData(), user.getBodyData()));
    }
}