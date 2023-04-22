package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.statuses.Status;

public class PlusMinusUse extends SyncedAttacker {

    public static final float DURATION = 3.0f;
    public static final float PROC_CD = 1.0f;
    public static final float CHAIN_DAMAGE = 15.0f;
    public static final int CHAIN_AMOUNT = 4;
    private static final int CHAIN_RADIUS = 15;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        ParticleEntity particle = new ParticleEntity(state, user, Particle.LIGHTNING_CHARGE, 1.0f, DURATION,
                true, SyncType.NOSYNC).setColor(HadalColor.SUNGLOW);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.HBOX);
        }

        user.getBodyData().addStatus(new Status(state, DURATION, false, user.getBodyData(), user.getBodyData()) {

            private float procCdCount = PROC_CD;
            @Override
            public void timePassing(float delta) {
                super.timePassing(delta);
                if (procCdCount >= PROC_CD) {
                    procCdCount -= PROC_CD;

                    SoundEffect.THUNDER.playSourced(state, user.getPixelPosition(), 0.5f);
                    user.getBodyData().addStatus(new Shocked(state, user.getBodyData(), user.getBodyData(),
                            CHAIN_DAMAGE, CHAIN_RADIUS, CHAIN_AMOUNT,
                            user.getHitboxFilter(), SyncedAttack.SHOCK_PLUS_MINUS));
                }
                procCdCount += delta;
            }
        });
    }
}