package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.TooltipManager;

/**
 * This mode setting is used for modes where the host can designate weapon drop mode
 * @author Xibberish Xenchilada
 */
public class SettingLoadoutMode extends ModeSetting {

    private static final String settingTag = "weapon_drops";
    private static final Integer defaultValue = 0;
    private static final UnlockEquip[] weaponDropLoadout = {UnlockEquip.SPEARGUN_NERFED, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    private SelectBox<String> dropsOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] loadoutChoices = UIText.SETTING_LOADOUT_MODE_OPTIONS.text().split(",");
        Text loadout = new Text(UIText.SETTING_LOADOUT_MODE.text());
        loadout.setScale(ModeSettingSelection.detailsScale);
        TooltipManager.addTooltip(loadout, UIText.SETTING_LOADOUT_MODE_DESC.text());

        dropsOptions = new SelectBox<>(GameStateManager.getSkin());
        dropsOptions.setItems(loadoutChoices);
        dropsOptions.setWidth(ModeSettingSelection.optionsWidth);
        dropsOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(loadout);
        table.add(dropsOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, dropsOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        mode.setLoadoutMode(indexToLoadoutMode(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue)));
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID, boolean justJoined) {
        if (state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 0) {
            for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
                if (weaponDropLoadout.length > i) {
                    newLoadout.multitools[i] = weaponDropLoadout[i];
                }
            }
        }
        if (state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 2) {
            for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
                if (weaponDropLoadout.length > i) {
                    newLoadout.multitools[i] = UnlockEquip.getRandWeapFromPool(state, "");
                }
            }
        }
    }

    private LoadoutMode indexToLoadoutMode(int index) {
        return switch (index) {
            case 1 -> LoadoutMode.CUSTOM;
            case 2 -> LoadoutMode.RANDOM;
            default -> LoadoutMode.CLASSIC;
        };
    }

    public enum LoadoutMode {
        CLASSIC,
        CUSTOM,
        RANDOM
    }
}
