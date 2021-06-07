package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSelection;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;

import java.util.Arrays;

public class SettingDroppableWeapons extends ModeSetting {

    private static final String settingTag = "weapon_drops";
    private static final Integer defaultValue = 1;
    private static final UnlockEquip[] weaponDropLoadout = {UnlockEquip.SPEARGUN_NERFED, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    private CheckBox dropsOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        dropsOptions = new CheckBox("WEAPON DROP MODE?",GameStateManager.getSkin());
        dropsOptions.setChecked(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1);
        table.add(dropsOptions).height(ModeSelection.detailHeight).pad(ModeSelection.detailPad).top().row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, dropsOptions.isChecked() ? 1 : 0);
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1) {
            state.setDroppableWeapons(true);
            state.setMapMultitools(Arrays.asList(weaponDropLoadout));
        }
    }
}
