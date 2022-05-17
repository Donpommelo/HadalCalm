package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.modifiers.ModeModifier;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 * This mode setting is used for modes that allow the host to select modifiers
 * @author Quothro Quebberish
 */
public class SetModifiers extends ModeSetting {

    private final ModeModifier[] modifiers;
    private static final HText ModifierNotifTag = HText.MODIFIER;

    public SetModifiers(ModeModifier... modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {

        Text title = new Text(ModifierNotifTag.text());
        title.setScale(ModeSettingSelection.detailsScale);

        //this gives an option to uncheck all modifiers
        Text uncheck = new Text(HText.MODIFIER_UNCHECK.text()).setButton(true);
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

        for (ModeModifier modifier : modifiers) {
            modifier.setSetting(state, mode, table);
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        for (ModeModifier modifier : modifiers) {
            modifier.saveSetting(state, mode);
        }
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        StringBuilder text = new StringBuilder(ModifierNotifTag.text());

        for (ModeModifier modifier : modifiers) {
            modifier.loadModifier(state, mode, text);
        }

        //this adds each active modifier to the initial notification that is displayed at thte start of the match
        if (!text.toString().equals(ModifierNotifTag.text())) {
            mode.getInitialNotifications().add(text.toString());
        }
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID, boolean justJoined) {
        for (ModeModifier modifier : modifiers) {
            if (state.getGsm().getSetting().getModeSetting(mode, modifier.getSettingTag(), 0) == 1) {
                modifier.processNewPlayerLoadout(state, mode, newLoadout, connID, justJoined);
            }
        }
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        for (ModeModifier modifier : modifiers) {
            if (state.getGsm().getSetting().getModeSetting(mode, modifier.getSettingTag(), 0) == 1) {
                modifier.modifyNewPlayer(state, mode, newLoadout, p, hitboxFilter);
            }
        }
    }
}
