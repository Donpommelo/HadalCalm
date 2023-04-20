package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;

public class SyncedAttacker {

    private SyncedAttack syncedAttack;

    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) { return null; }

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) { return null; }

    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {}

    public SyncedAttack getSyncedAttack() { return syncedAttack; }

    public void setSyncedAttack(SyncedAttack syncedAttack) { this.syncedAttack = syncedAttack; }
}
