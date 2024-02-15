package com.mygdx.hadal.map;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes with a set list of weapons
 * @author Fluitcake Flathworth
 */
public class SetLoadoutEquips extends ModeSetting {

    private final Array<UnlockEquip> mapWeapons = new Array<>();

    public SetLoadoutEquips(UnlockEquip... weapons) {
        mapWeapons.addAll(weapons);
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
            if (mapWeapons.size > i) {
                newLoadout.multitools[i] = mapWeapons.get(i);
            }
        }
    }
}
