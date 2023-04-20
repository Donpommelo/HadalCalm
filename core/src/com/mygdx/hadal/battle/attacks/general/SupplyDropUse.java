package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class SupplyDropUse extends SyncedAttacker {

    private static final float EQUIP_DROP_LIFEPAN = 10.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC1_ACTIVE.playSourced(state, startPosition, 1.0f);
        new PickupEquip(state, startPosition, UnlockEquip.getRandWeapFromPool(state, ""), EQUIP_DROP_LIFEPAN);
    }
}