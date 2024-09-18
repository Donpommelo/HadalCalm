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
import com.mygdx.hadal.statuses.StatChangeStatus;

public class ReservedFuelUse extends SyncedAttacker {

    public static final float DURATION = 5.0f;
    public static final float POWER = 18.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC2_FUEL.playSourced(state, user.getPixelPosition(), 0.5f);
        ParticleEntity particle = new ParticleEntity(state, user, Particle.BRIGHT, 1.0f, DURATION,
                true, SyncType.NOSYNC).setColor(HadalColor.FRENCH_LIME);

        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.EFFECT);
        }

        user.getBodyData().addStatus(new StatChangeStatus(state, DURATION, Stats.FUEL_REGEN, POWER, user.getBodyData(), user.getBodyData()));
    }
}