package com.mygdx.hadal.map;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This mode setting is used for modes with a set list of weapons
 * @author Fluitcake Flathworth
 */
public class SetLoadoutEquips extends ModeSetting {

    private final ArrayList<UnlockEquip> mapWeapons = new ArrayList<>();

    public SetLoadoutEquips(UnlockEquip... weapons) {
        mapWeapons.addAll(Arrays.asList(weapons));
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
            if (mapWeapons.size() > i) {
                newLoadout.multitools[i] = mapWeapons.get(i);
            }
        }
    }
}
