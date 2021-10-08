package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.map.modifiers.ModeModifier;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes that allow the host to select modifiers
 * @author Quothro Quebberish
 */
public class SetModifiers extends ModeSetting {

    private final ModeModifier[] modifiers;
    private static final String ModifierNotifTag = "MODIFIERS: ";

    public SetModifiers(ModeModifier... modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {

        Text title = new Text("MODIFIERS", 0, 0, false);
        title.setScale(ModeSettingSelection.detailsScale);
        Text uncheck = new Text("UNCHECK ALL?", 0, 0, true);
        uncheck.setScale(ModeSettingSelection.detailsScale);

        uncheck.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                for (ModeModifier modifier: modifiers) {
                    modifier.setCheck(false);
                }
            }
        });

        table.add(title).height(ModeSettingSelection.detailHeightSmall).pad(ModeSettingSelection.detailPad).top();
        table.add(uncheck).height(ModeSettingSelection.detailHeightSmall).pad(ModeSettingSelection.detailPad).row();

        for (ModeModifier modifier: modifiers) {
            modifier.setSetting(state, mode, table);
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        for (ModeModifier modifier: modifiers) {
            modifier.saveSetting(state, mode);
        }
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        StringBuilder text = new StringBuilder(ModifierNotifTag);

        for (ModeModifier modifier: modifiers) {
            modifier.loadModifier(state, mode, text);
        }
        if (!text.toString().equals(ModifierNotifTag)) {
            mode.getInitialNotifications().add(text.toString());
        }
    }
}
