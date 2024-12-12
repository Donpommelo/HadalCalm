package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class DepthChargeUse extends SyncedAttacker {

    public static final float DURATION = 1.0f;
    public static final float PROC_CD = 0.25f;
    private static final float RECOIL = 50.0f;

    public static final float EXPLOSION_DAMAGE = 40.0f;
    private static final Vector2 EXPLOSION_SIZE = new Vector2(300, 300);
    private static final float EXPLOSION_KNOCKBACK = 20.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        user.pushMomentumMitigation(0, RECOIL);

        user.getBodyData().addStatus(new Status(state, DURATION, false, user.getBodyData(), user.getBodyData()) {

            private float procCdCount = PROC_CD;
            private final Vector2 explosionPos = new Vector2(user.getPixelPosition());
            @Override
            public void timePassing(float delta) {
                super.timePassing(delta);
                if (procCdCount >= PROC_CD) {
                    procCdCount -= PROC_CD;

                    SoundManager.play(state, new SoundLoad(SoundEffect.EXPLOSION6)
                            .setVolume(0.8f)
                            .setPosition(explosionPos));

                    WeaponUtils.createExplosion(state, explosionPos, EXPLOSION_SIZE.x, user, EXPLOSION_DAMAGE,
                            EXPLOSION_KNOCKBACK, user.getHitboxFilter(), true, DamageSource.DEPTH_CHARGE);
                    explosionPos.sub(0, EXPLOSION_SIZE.x / 2);
                }
                procCdCount += delta;
            }
        });
    }
}