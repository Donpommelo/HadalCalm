package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;

/**
 * A Synced Attacker creates the Synced attacks that are sent between server and client.
 *
 */
public class SyncedAttacker {

    //the synced attack that this synced attacker will create
    //atm, used for synced attackers that can correspond to a variety of different sources (like shock)
    private SyncedAttack syncedAttack;

    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) { return null; }

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) { return null; }

    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {}

    public SyncedAttack getSyncedAttack() { return syncedAttack; }

    public void setSyncedAttack(SyncedAttack syncedAttack) { this.syncedAttack = syncedAttack; }
}
