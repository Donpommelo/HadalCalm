package com.mygdx.hadal.map.modifiers;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.ModeSetting;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.TooltipManager;

/**
 * A Mode Modifier allows the player to select a setting that changes the gameplay of the match
 * Mode-specific modifiers are saved after starting a match
 * @author Thugger Triburger
 */
public class ModeModifier extends ModeSetting {

    private final String settingTag;
    private final UIText uiText, name;
    private UIText desc;

    private CheckBox dropsOptions;

    public ModeModifier(String settingTag, UIText uiText, UIText name) {
        this.settingTag = settingTag;
        this.uiText = uiText;
        this.name = name;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        Text title = new Text(uiText.text());
        title.setScale(ModeSettingSelection.detailsScale);

        if (desc != null) {
            TooltipManager.addTooltip(title, desc.text());
        }

        dropsOptions = new CheckBox("", GameStateManager.getSkin());
        dropsOptions.setChecked(state.getGsm().getSetting().getModeSetting(mode, settingTag, 0) == 1);
        table.add(title);
        table.add(dropsOptions).height(ModeSettingSelection.detailHeightSmall).pad(ModeSettingSelection.detailPad).top().row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, dropsOptions.isChecked() ? 1 : 0);
    }

    /**
     * mode modifiers process their logic in a separate function so that it can modify the string builder used
     * for initial notification
     */
    public void loadModifier(PlayState state, GameMode mode, StringBuilder text) {
        if (state.getGsm().getSetting().getModeSetting(mode, settingTag, 0) == 1) {
            executeModifier(state);
            text.append(name.text()).append(", ");
        }
    }

    public void executeModifier(PlayState state) {}

    public void setCheck(boolean check) {
        if (dropsOptions != null) {
            dropsOptions.setChecked(check);
        }
    }

    public String getSettingTag() { return settingTag; }

    public void setDesc(UIText desc) { this.desc = desc; }
}
