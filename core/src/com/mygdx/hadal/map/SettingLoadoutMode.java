package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.TooltipManager;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * This mode setting is used for modes where the host can designate weapon drop mode
 * @author Xibberish Xenchilada
 */
public class SettingLoadoutMode extends ModeSetting {

    private static final UnlockEquip[] weaponDropLoadout = {UnlockEquip.SPEARGUN_NERFED, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    private SelectBox<String> dropsOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] loadoutChoices = UIText.SETTING_LOADOUT_MODE_OPTIONS.text().split(",");
        Text loadout = new Text(UIText.SETTING_LOADOUT_MODE.text());
        loadout.setScale(UIHub.DETAILS_SCALE);
        TooltipManager.addTooltip(loadout, UIText.SETTING_LOADOUT_MODE_DESC.text());

        dropsOptions = new SelectBox<>(SKIN);
        dropsOptions.setItems(loadoutChoices);
        dropsOptions.setWidth(UIHub.OPTIONS_WIDTH);
        dropsOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.WEAPON_DROPS));

        table.add(loadout);
        table.add(dropsOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        JSONManager.setting.setModeSetting(mode, SettingSave.WEAPON_DROPS, dropsOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        mode.setLoadoutMode(indexToLoadoutMode(JSONManager.setting.getModeSetting(mode, SettingSave.WEAPON_DROPS)));
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        if (JSONManager.setting.getModeSetting(mode, SettingSave.WEAPON_DROPS) == 0) {
            for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
                if (weaponDropLoadout.length > i) {
                    newLoadout.multitools[i] = weaponDropLoadout[i];
                }
            }
        }
        if (JSONManager.setting.getModeSetting(mode, SettingSave.WEAPON_DROPS) == 2) {
            for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
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
