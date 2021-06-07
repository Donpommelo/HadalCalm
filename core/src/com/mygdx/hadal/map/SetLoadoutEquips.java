package com.mygdx.hadal.map;

import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.Arrays;

public class SetLoadoutEquips extends ModeSetting {

    private final ArrayList<UnlockEquip> mapWeapons = new ArrayList<>();

    public SetLoadoutEquips(UnlockEquip... weapons) {
        mapWeapons.addAll(Arrays.asList(weapons));
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setMapMultitools(mapWeapons);
    }
}
