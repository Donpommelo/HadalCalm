package com.mygdx.hadal.map;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

/**
 */
public class ToggleWeaponDrops extends ModeSetting {

    private static final float equipDropLifepan = 7.5f;

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            UnlockEquip equip = vic.getUser().getLoadoutManager().getActiveLoadout().multitools[vic.getEquipHelper().getCurrentSlot()];
            if (!UnlockEquip.NOTHING.equals(equip) && !UnlockEquip.SPEARGUN_NERFED.equals(equip)) {
                new PickupEquip(vic.getState(), vic.getPixelPosition(), equip, equipDropLifepan);
            }
        }
    }
}
