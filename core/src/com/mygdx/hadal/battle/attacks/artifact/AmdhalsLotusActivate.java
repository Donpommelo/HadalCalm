package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;

public class AmdhalsLotusActivate extends SyncedAttacker {

    public static final float HP_REGEN_BUFF = 40.0f;
    public static final float FUEL_REGEN_BUFF = 15.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC18_BUFF.playSourced(state, user.getPixelPosition(), 0.5f);

        ((Player) user).getArtifactIconHelper().addArtifactFlash(UnlockArtifact.AMDAHLS_LOTUS);

        ParticleEntity particle = new ParticleEntity(state, user, Particle.RING, 1.0f, 1.0f, true, SyncType.NOSYNC)
                .setScale(0.4f);

        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.HBOX);
        }

        user.getBodyData().addStatus(new StatusComposite(state, state.getTimerManager().getTimer(), false, user.getBodyData(), user.getBodyData(),
                new StatChangeStatus(state, Stats.FUEL_REGEN, FUEL_REGEN_BUFF, user.getBodyData()),
                new StatChangeStatus(state, Stats.HP_REGEN, HP_REGEN_BUFF, user.getBodyData())));
    }
}