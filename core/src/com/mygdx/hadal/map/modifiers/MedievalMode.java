package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;

import java.util.Arrays;

/**
 * @author Nemongrass Nurlando
 */
public class MedievalMode extends ModeModifier {

    private static final String settingTag = "medieval_mode";
    private static final String uiText = "MEDIEVAL MODE?";
    private static final String name = "MEDIEVAL";

    private static final UnlockEquip[] BaseEquip = {UnlockEquip.SCRAPRIPPER, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    public MedievalMode() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.setMapMultitools(Arrays.asList(BaseEquip));
        state.setMapActiveItem(UnlockActives.JUMP_KICK);
        state.addMapEquipTag(UnlockTag.MEDIEVAL);
    }
}
