package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * This mode setting is used for modes where the player can set their base Hp
 * @author Doltfield Desmith
 */
public class SettingBaseHp extends ModeSetting {

    private SelectBox<String> hpOptions;

    private int baseHpIndex;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] hpChoices = UIText.SETTING_BASE_HP_OPTIONS.text().split(",");
        Text hp = new Text(UIText.SETTING_BASE_HP.text());
        hp.setScale(UIHub.DETAILS_SCALE);

        hpOptions = new SelectBox<>(SKIN);
        hpOptions.setItems(hpChoices);
        hpOptions.setWidth(UIHub.OPTIONS_WIDTH);
        hpOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.BASE_HP));

        table.add(hp);
        table.add(hpOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        JSONManager.setting.setModeSetting(mode, SettingSave.BASE_HP, hpOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        baseHpIndex = JSONManager.setting.getModeSetting(mode, SettingSave.BASE_HP);
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {

        //note that this is run independently on the client
        p.setBaseHp(indexToHp(baseHpIndex));
    }

    public static int indexToHp(int index) {
        return switch (index) {
            case 1 -> 125;
            case 2 -> 150;
            case 3 -> 175;
            case 4 -> 200;
            default -> 100;
        };
    }
}
