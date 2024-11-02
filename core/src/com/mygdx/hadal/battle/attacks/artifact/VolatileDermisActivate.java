package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;

public class VolatileDermisActivate extends SyncedAttacker {

    public static final float CHAIN_DAMAGE = 15.0f;
    public static final int CHAIN_AMOUNT = 3;
    private static final int CHAIN_RADIUS = 10;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.THUNDER)
                .setVolume(0.5f)
                .setPosition(startPosition));

        user.getBodyData().addStatus(new Shocked(state, user.getBodyData(), user.getBodyData(),
                CHAIN_DAMAGE, CHAIN_RADIUS, CHAIN_AMOUNT, user.getHitboxFilter(),
                SyncedAttack.SHOCK_DERMIS));
    }
}