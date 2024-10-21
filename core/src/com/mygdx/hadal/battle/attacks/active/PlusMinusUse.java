package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Schmuck;
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
        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.LIGHTNING_CHARGE, user)
                .setLifespan(DURATION)
                .setColor(HadalColor.SUNGLOW));

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