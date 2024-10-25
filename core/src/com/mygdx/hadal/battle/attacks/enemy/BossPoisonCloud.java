package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class BossPoisonCloud extends SyncedAttacker {

    private static final Vector2 POISON_SIZE = new Vector2(150, 280);
    private static final float POISON_DAMAGE = 0.6f;
    private static final float POISON_DURATION = 7.5f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        if (extraFields.length > 0) {
            if (0 == extraFields[0]) {
                SoundEffect.DARKNESS2.playSourced(state, startPosition, 0.4f);
            }
        }

        Poison poison = new Poison(state, startPosition, POISON_SIZE, POISON_DAMAGE, POISON_DURATION, user, true,
                user.getHitboxFilter(), DamageSource.ENEMY_ATTACK);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(poison.getEntityID(), poison, false, ObjectLayer.EFFECT);
        }
    }
}
