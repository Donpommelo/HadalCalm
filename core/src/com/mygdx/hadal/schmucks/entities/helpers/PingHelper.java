package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class PingHelper {

    private static final float PING_CD = 1.0f;

    private final PlayState state;
    private final Player player;
    private float pingCdCount;

    public PingHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;
    }

    public void controller(float delta) {
        pingCdCount -= delta;
    }

    /**
     * Player pings at mouse location
     */
    private static final Vector2 NOTIF_OFFSET = new Vector2(0, 35);
    public void ping() {
        if (pingCdCount < 0) {
            pingCdCount = PING_CD;
            SyncedAttack.PING.initiateSyncedAttackSingle(state, player,
                    player.getMouseHelper().getPixelPosition().add(NOTIF_OFFSET), new Vector2());
        }
    }
}
