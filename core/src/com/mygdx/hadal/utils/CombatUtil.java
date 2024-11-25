package com.mygdx.hadal.utils;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.helpers.PlayerSpriteHelper.DespawnType;

/**
 * This contains utility functions used whe processing combat.
 * atm, this just includes death animation processing
 */
public class CombatUtil {

    /**
     * This parses the cause of death (source and any tags of the lethal instance of damage) to return the despawn type
     */
    public static DespawnType getDespawnType(DamageSource source, DamageTag... tags) {
        DespawnType type = DespawnType.GIB;

        //in the case of a disconnect, this is a special death with teleport particles instead of frags
        if (source == DamageSource.DISCONNECT) {
            type = DespawnType.TELEPORT;
        } else {
            for (DamageTag tag : tags) {
                if (tag == DamageTag.FIRE || tag == DamageTag.ENERGY) {
                    type = DespawnType.VAPORIZE;
                    break;
                }
                if (tag == DamageTag.CUTTING) {
                    type = DespawnType.BIFURCATE;
                    break;
                }
            }
        }

        return type;
    }
}
