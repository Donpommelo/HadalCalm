package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where the host can designate weapon drop mode
 * @author Xibberish Xenchilada
 */
public class SettingDroppableWeapons extends ModeSetting {

    private static final String settingTag = "weapon_drops";
    private static final Integer defaultValue = 1;
    private static final UnlockEquip[] weaponDropLoadout = {UnlockEquip.SPEARGUN_NERFED, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    private static final float equipDropLifepan = 12.0f;
    private CheckBox dropsOptions;
    private boolean droppableWeapons;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        dropsOptions = new CheckBox("WEAPON DROP MODE?",GameStateManager.getSkin());
        dropsOptions.setChecked(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1);
        table.add(dropsOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).top().row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, dropsOptions.isChecked() ? 1 : 0);
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        droppableWeapons = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1;
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        if (state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1) {
            for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
                if (weaponDropLoadout.length > i) {
                    newLoadout.multitools[i] = weaponDropLoadout[i];
                }
            }
        }
    }

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic) {

        //in weapon drop mode, players will drop their currently held weapon on death (unless it is default weapon or nothing)
        if (vic != null && droppableWeapons) {
            UnlockEquip equip = vic.getPlayerData().getLoadout().multitools[vic.getPlayerData().getCurrentSlot()];
            if (!equip.equals(UnlockEquip.NOTHING) && !equip.equals(UnlockEquip.SPEARGUN_NERFED)) {
                new PickupEquip(state, vic.getPixelPosition(), equip, equipDropLifepan);
            }
        }
    }
}
