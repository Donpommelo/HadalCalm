package com.mygdx.hadal.utils;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.helpers.PlayerSpriteHelper.DespawnType;

public class CombatUtil {

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
