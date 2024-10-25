package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class HealingFieldUse extends SyncedAttacker {

    private final UnlockArtifact effectSource;

    public HealingFieldUse(UnlockArtifact effectSource) {
        this.effectSource = effectSource;
    }

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {

        if (effectSource.equals(UnlockArtifact.NUMBER_ONE_BOSS_MUG)) {
            ((Player) user).getArtifactIconHelper().addArtifactFlash(UnlockArtifact.NUMBER_ONE_BOSS_MUG);
        }

        Vector2 fieldSize = new Vector2();
        float heal = 0.0f;
        float duration = 0.0f;
        if (2 < extraFields.length) {
            fieldSize.set(extraFields[0], extraFields[0]);
            heal = extraFields[1];
            duration = extraFields[2];
        }
        HealingArea healingArea = new HealingArea(state, startPosition, fieldSize, heal, duration, user, (short) 0);
        EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.MAGIC21_HEAL, healingArea)
                .setLifespan(duration)
                .setVolume(0.25f));

        if (!state.isServer()) {
            ((ClientState) state).addEntity(healingArea.getEntityID(), healingArea, false, ObjectLayer.EFFECT);
        }
    }
}