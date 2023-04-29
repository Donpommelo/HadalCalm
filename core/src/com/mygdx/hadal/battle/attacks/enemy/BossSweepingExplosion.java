package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class BossSweepingExplosion extends SyncedAttacker {

    private static final float EXPLOSION_DAMAGE = 35.0f;
    private static final float EXPLOSION_KNOCKBACK = 35.0f;
    private static final float EXPLOSION_SIZE = 300;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.EXPLOSION6.playSourced(state, startPosition, 0.5f);
        WeaponUtils.createExplosion(state, startPosition, EXPLOSION_SIZE, user, EXPLOSION_DAMAGE,
                EXPLOSION_KNOCKBACK, user.getHitboxFilter(), true, DamageSource.ENEMY_ATTACK);
    }
}
