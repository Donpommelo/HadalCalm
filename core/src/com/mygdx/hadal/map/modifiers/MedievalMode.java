package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 * In medieval mode, only melee weapons + love bow can be used
 * @author Nemongrass Nurlando
 */
public class MedievalMode extends ModeModifier {

    private static final String settingTag = "medieval_mode";
    private static final HText uiText = HText.MODIFIER_MEDIEVAL_UI;
    private static final HText name = HText.MODIFIER_MEDIEVAL;

    private static final UnlockEquip[] BaseEquip = {UnlockEquip.SCRAPRIPPER, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    public MedievalMode() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) {
        state.addMapEquipTag(UnlockTag.MEDIEVAL);
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID, boolean justJoined) {
        for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
            if (BaseEquip.length > i) {
                newLoadout.multitools[i] = BaseEquip[i];
            }
        }
        newLoadout.activeItem = UnlockActives.JUMP_KICK;
    }
}
