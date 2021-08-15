package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.map.modifiers.ModeModifier;
import com.mygdx.hadal.states.PlayState;

public class SetModifiers extends ModeSetting {

    private final ModeModifier[] modifiers;

    public SetModifiers(ModeModifier... modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {

        Text title = new Text("MODIFIERS", 0, 0, false);
        title.setScale(ModeSettingSelection.detailsScale);
        table.add(title).colspan(2).row();

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
        StringBuilder text = new StringBuilder("MODIFIERS: ");

        for (ModeModifier modifier: modifiers) {
            modifier.loadSettingMisc(state, mode);
            text.append(modifier.getName()).append(", ");
        }
        mode.getInitialNotifications().add(text.toString());
    }
}
