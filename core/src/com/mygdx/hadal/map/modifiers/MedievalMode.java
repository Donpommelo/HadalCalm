package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * In medieval mode, only melee weapons + love bow can be used
 * @author Nemongrass Nurlando
 */
public class MedievalMode extends ModeModifier {

    private static final String settingTag = "medieval_mode";
    private static final UIText uiText = UIText.MODIFIER_MEDIEVAL_UI;
    private static final UIText name = UIText.MODIFIER_MEDIEVAL;
    private static final UIText desc = UIText.MODIFIER_MEDIEVAL_DESC;

    private static final UnlockEquip[] BaseEquip = {UnlockEquip.SCRAPRIPPER, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    public MedievalMode() {
        super(settingTag, uiText, name);
        this.setDesc(desc);
    }

    @Override
    public void executeModifier(PlayState state) {
        state.addMapEquipTag(UnlockTag.MEDIEVAL);
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID, boolean justJoined) {
        for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
            if (BaseEquip.length > i) {
                newLoadout.multitools[i] = BaseEquip[i];
            }
        }
        newLoadout.activeItem = UnlockActives.JUMP_KICK;
    }
}
