package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class ArtifactIconActivate extends SyncedAttacker {

    private final UnlockArtifact effectSource;

    public ArtifactIconActivate(UnlockArtifact effectSource) {
        this.effectSource = effectSource;
    }

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        ((Player) user).getArtifactIconHelper().addArtifactFlash(effectSource);
    }
}