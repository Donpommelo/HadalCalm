package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;

public class SkippersBoxOfFunActivate extends SyncedAttacker {

    private static final float DURATION = 1.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC27_EVIL.playSourced(state, user.getPixelPosition(), 0.5f);

        ParticleEntity particle =  new ParticleEntity(state, user, Particle.SMOKE_TOTLC, 1.0f, DURATION, true, SyncType.NOSYNC);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.HBOX);
        }

        if (user instanceof Player player) {
            Equippable equip = UnlocktoItem.getUnlock(UnlockEquip.getRandWeapFromPool(state, ""), null);
            player.getPlayerData().pickup(equip);
        }
    }
}