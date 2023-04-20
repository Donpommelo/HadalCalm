package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class HealingFieldUse extends SyncedAttacker {

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {

        Vector2 fieldSize = new Vector2();
        float heal = 0.0f;
        float duration = 0.0f;
        if (2 < extraFields.length) {
            fieldSize.set(extraFields[0], extraFields[0]);
            heal = extraFields[1];
            duration = extraFields[2];
        }
        HealingArea healingArea = new HealingArea(state, startPosition, fieldSize, heal, duration, user, (short) 0);
        SoundEntity sound = new SoundEntity(state, healingArea, SoundEffect.MAGIC21_HEAL, duration, 0.25f, 1.0f, true, true,
                SyncType.NOSYNC);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(healingArea.getEntityID(), healingArea, false, ClientState.ObjectLayer.EFFECT);
            ((ClientState) state).addEntity(sound.getEntityID(), sound, false, ClientState.ObjectLayer.EFFECT);
        }
    }
}