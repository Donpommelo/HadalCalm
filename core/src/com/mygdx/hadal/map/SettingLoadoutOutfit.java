package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.TooltipManager;

/**
 */
public class SettingLoadoutOutfit extends ModeSetting {

    public static final String settingTag = "loadout_outfit";
    public static final Integer defaultValue = 0;

    private SelectBox<String> outfitOptions;

    private Loadout universalLoadout;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {

        String[] optionChoices = new String[state.getGsm().getSavedOutfits().getOutfits().size + 1];
        optionChoices[0] = UIText.SETTING_OUTFIT_DEFAULT.text();
        for (int i = 1; i < optionChoices.length; i++) {
            optionChoices[i] = state.getGsm().getSavedOutfits().getOutfits().keys().toArray().get(i - 1);
        }

        Text outfit = new Text(UIText.SETTING_OUTFIT.text());
        outfit.setScale(UIHub.DETAILS_SCALE);
        TooltipManager.addTooltip(outfit, UIText.SETTING_OUTFIT_DESC.text());

        outfitOptions = new SelectBox<>(GameStateManager.getSkin());
        outfitOptions.setItems(optionChoices);
        outfitOptions.setWidth(UIHub.OPTIONS_WIDTH);
        if (optionChoices.length > state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue)) {
            outfitOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));
        }

        table.add(outfit);
        table.add(outfitOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, outfitOptions.getSelectedIndex());
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        int startOutfit = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
        if (startOutfit != 0) {
            if (state.getGsm().getSavedOutfits().getOutfits().get(outfitOptions.getSelected()) != null) {
                universalLoadout = new Loadout(state.getGsm().getSavedOutfits().getOutfits().get(outfitOptions.getSelected()));
            }
        }
        return "";
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        int startOutfit = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
        if (startOutfit != 0) {
            mode.getInitialNotifications().add(UIText.SETTING_OUTFIT_NOTIF.text());
        }
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID, boolean justJoined) {
        int startOutfit = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
        if (startOutfit != 0) {
            System.arraycopy(universalLoadout.multitools, 0, newLoadout.multitools, 0, Loadout.MAX_WEAPON_SLOTS);
            System.arraycopy(universalLoadout.artifacts, 0, newLoadout.artifacts, 0, Loadout.MAX_ARTIFACT_SLOTS);
            newLoadout.activeItem = universalLoadout.activeItem;
        }
    }
}
