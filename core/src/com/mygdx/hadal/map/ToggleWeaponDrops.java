package com.mygdx.hadal.map;

import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 */
public class ToggleWeaponDrops extends ModeSetting {

    private static final float equipDropLifepan = 10.0f;

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageTypes... tags) {
        PlayerBodyData player = vic.getPlayerData();
        UnlockEquip equip = player.getLoadout().multitools[player.getCurrentSlot()];
        if (!equip.equals(UnlockEquip.NOTHING) && !equip.equals(UnlockEquip.SPEARGUN_NERFED)) {
            new PickupEquip(vic.getState(), vic.getPixelPosition(), equip, equipDropLifepan);
        }
    }
}
