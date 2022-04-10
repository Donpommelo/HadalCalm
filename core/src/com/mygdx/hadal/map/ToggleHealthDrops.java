package com.mygdx.hadal.map;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

/**
 */
public class ToggleHealthDrops extends ModeSetting {

    private static final float heal = 0.25f;
    private static final float chance = 0.15f;

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            if (MathUtils.randomBoolean(chance)) {
                WeaponUtils.createPickup(state, vic.getPixelPosition(), WeaponUtils.pickupTypes.HEALTH, heal);
            }
        }
    }
}
