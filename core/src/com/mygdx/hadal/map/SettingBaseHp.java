package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * This mode setting is used for modes where the player can set their base Hp
 * @author Doltfield Desmith
 */
public class SettingBaseHp extends ModeSetting {

    private static final String settingTag = "base_hp";
    private static final Integer defaultValue = 2;

    private SelectBox<String> hpOptions;

    private int baseHpIndex;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] hpChoices = UIText.SETTING_BASE_HP_OPTIONS.text().split(",");
        Text hp = new Text(UIText.SETTING_BASE_HP.text());
        hp.setScale(UIHub.DETAILS_SCALE);

        hpOptions = new SelectBox<>(GameStateManager.getSkin());
        hpOptions.setItems(hpChoices);
        hpOptions.setWidth(UIHub.OPTIONS_WIDTH);
        hpOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(hp);
        table.add(hpOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, hpOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        baseHpIndex = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
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
