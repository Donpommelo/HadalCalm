package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.PickupVacuum;

public class PickupUtils {

    private static final float EGGPLANT_VELO = 7.5f;
    /**
     * This spawns some amount of scrap events as currency for the player
     *
     * @param statCheck: do we take into account the player's bonus scrap drop?
     * @param score: does picking up the screp increment the player's score?
     */
    public static void spawnScrap(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                  int amount, boolean statCheck, boolean score) {
        if (!state.isServer()) { return; }
        float countScore = score ? 1.0f : 0.0f;

        int modifiedAmount = amount;

        if (user instanceof Player player) {
            if (statCheck && player.getPlayerData() != null) {
                if (player.getPlayerData().getStat(Stats.EXTRA_SCRAP) * amount < 1.0f
                        && player.getPlayerData().getStat(Stats.EXTRA_SCRAP) > 0) {
                    modifiedAmount = amount + 1;
                } else {
                    modifiedAmount = (int) (amount * (1 + player.getPlayerData().getStat(Stats.EXTRA_SCRAP)));
                }
            }
        }

        Vector2[] positions = new Vector2[modifiedAmount];
        Vector2[] velocities = new Vector2[modifiedAmount];
        for (int i = 0; i < modifiedAmount; i++) {
            positions[i] = startPosition;
            velocities[i] = startVelocity.nor().scl(EGGPLANT_VELO);
        }

        SyncedAttack.EGGPLANT.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities, countScore);
    }

    private static final float CANDY_VELO = 7.5f;
    public static void spawnCandy(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, int amount) {
        Vector2[] positions = new Vector2[amount];
        Vector2[] velocities = new Vector2[amount];
        for (int i = 0; i < amount; i++) {
            positions[i] = startPosition;
            velocities[i] = startVelocity.nor().scl(CANDY_VELO);
        }
        for (Hitbox hbox : SyncedAttack.CANDY.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities)) {
            hbox.addStrategy(new PickupVacuum(state, hbox, user.getBodyData()));
        }
    }
}
